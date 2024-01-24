package ng.sae.songs223

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import java.io.File
import java.io.InputStream

fun getHeaderLengthFromVideo(songFileName: File, context: Context): Int {
    var headerLengthStr = ""
    val songInputStream = songFileName.inputStream()
    while (true) {
        val buffer = ByteArray(1)
        songInputStream.read(buffer)
        if (String(buffer) != "\n") {
            headerLengthStr += String(buffer)
        } else {
            break
        }
    }

    songInputStream.close()
    return headerLengthStr.toInt()
}

data class VideoHeader (
    val LaptopUniqueFrames: List<List<Int>>,
    val LaptopFrames: Map<Int,Int>,
    val MobileUniqueFrames: List<List<Int>>,
    val MobileFrames: Map<Int,Int>,
    val AudioSize: Int,
    val LaptopVideoSize: Int,
    val MobileVideoSize: Int,
)

fun readHeaderFromVideo(songFileName: File, context: Context): VideoHeader {
    val headerLength = getHeaderLengthFromVideo(songFileName, context)
    val songInputStream = songFileName.inputStream()
    val buffer = ByteArray(headerLength)
    val headerOffset = headerLength.toString().length + 1
    songInputStream.skip(headerOffset.toLong())
    songInputStream.read(buffer, 0, headerLength)
    songInputStream.close()

    val headerStr = String(buffer)

    val luniqueFramesBeginPart = headerStr.indexOf("laptop_unique_frames:")
    val luniqueFramesEndPart = headerStr.substring(luniqueFramesBeginPart).indexOf("::")
    val luniqueFramesPart = headerStr.substring(luniqueFramesBeginPart+"laptop_unique_frames:\n".length,
        luniqueFramesBeginPart+luniqueFramesEndPart)
    val luniqueFrames = ArrayList<List<Int>>()
    for (line in luniqueFramesPart.lines()) {
        val templine = line.trim()
        if (templine == "") {
            continue
        }
        val partsOfLine = templine.split(":")
        val tempList = listOf(partsOfLine[0].toInt(), partsOfLine[1].trim().toInt())
        luniqueFrames.add(tempList)
    }

    val lframesBeginPart = headerStr.indexOf("laptop_frames:")
    val lframesEndPart = headerStr.substring(lframesBeginPart).indexOf("::")
    val lframesPart = headerStr.substring(lframesBeginPart+"laptop_frames:\n".length,
        lframesBeginPart+lframesEndPart)
    val lframes = HashMap<Int, Int>()
    for (line in lframesPart.lines()) {
        val templine = line.trim()
        if (templine == "") {
            continue
        }
        val partsOfLine = templine.split(":")
        lframes[partsOfLine[0].toInt()] = partsOfLine[1].trim().toInt()
    }

    val muniqueFramesBeginPart = headerStr.indexOf("mobile_unique_frames:")
    val muniqueFramesEndPart = headerStr.substring(muniqueFramesBeginPart).indexOf("::")
    val muniqueFramesPart = headerStr.substring(muniqueFramesBeginPart+"mobile_unique_frames:\n".length,
        muniqueFramesBeginPart+muniqueFramesEndPart)
    val muniqueFrames = ArrayList<List<Int>>()
    for (line in muniqueFramesPart.lines()) {
        val templine = line.trim()
        if (templine == "") {
            continue
        }
        val partsOfLine = templine.split(":")
        val tempList = listOf(partsOfLine[0].toInt(), partsOfLine[1].trim().toInt())
        muniqueFrames.add(tempList)
    }

    val mframesBeginPart = headerStr.indexOf("mobile_frames:")
    val mframesEndPart = headerStr.substring(mframesBeginPart).indexOf("::")
    val mframesPart = headerStr.substring(mframesBeginPart+"mobile_frames:\n".length,
        mframesBeginPart+mframesEndPart)
    val mframes = HashMap<Int, Int>()
    for (line in mframesPart.lines()) {
        val templine = line.trim()
        if (templine == "") {
            continue
        }
        val partsOfLine = templine.split(":")
        mframes[partsOfLine[0].toInt()] = partsOfLine[1].trim().toInt()
    }

    val binaryBeginPart = headerStr.indexOf("binary:")
    val binaryEndPart = headerStr.substring(binaryBeginPart).indexOf("::")
    val binaryPart = headerStr.substring(binaryBeginPart+"binary:\n".length,
        binaryBeginPart+binaryEndPart)
    val lines = binaryPart.lines()
    val audioSize = lines[0].substring("audio: ".length).toInt()
    val lVideoSize = lines[1].substring("laptop_frames_lump: ".length).toInt()
    val mVideoSize = lines[2].substring("mobile_frames_lump: ".length).toInt()

    return VideoHeader(luniqueFrames, lframes, muniqueFrames, mframes, audioSize, lVideoSize, mVideoSize)
}

fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun readAudio(songFileName: File, context: Context): Uri {
    val headerLength = getHeaderLengthFromVideo(songFileName, context)
    val audioOffset = headerLength + 1 + headerLength.toString().length
    val videoHeader = readHeaderFromVideo(songFileName, context)
    val songInputStream = songFileName.inputStream()
    val buffer = ByteArray(videoHeader.AudioSize)
    songInputStream.skip(audioOffset.toLong())
    songInputStream.read(buffer, 0, videoHeader.AudioSize)
    songInputStream.close()

    val outputDir = context.cacheDir
    val tmpAudioFile = File(outputDir, "tmp_audio.mp3")
    tmpAudioFile.delete()
    tmpAudioFile.writeBytes(buffer)
    return tmpAudioFile.toUri()
}

fun getVideoLength(songFileName: File, context: Context): Int {
    val videoHeader = readHeaderFromVideo(songFileName, context)
    return videoHeader.MobileFrames.size
}

fun readMobileFrames(songFileName: File, context: Context, seconds: Int): Uri {
    val headerLength = getHeaderLengthFromVideo(songFileName, context)
    val videoHeader = readHeaderFromVideo(songFileName, context)
    val songInputStream = songFileName.inputStream()

    val buffer = ByteArray(videoHeader.MobileVideoSize)
    val audioOffset = headerLength + 1 + headerLength.toString().length
    val videoOffset = audioOffset + videoHeader.AudioSize + videoHeader.LaptopVideoSize
    songInputStream.skip(videoOffset.toLong())
    songInputStream.read(buffer, 0, videoHeader.MobileVideoSize)

    val allFrames = ArrayList<Int>()
    for (entry in videoHeader.MobileFrames) {
        allFrames.add(entry.key)
    }
    allFrames.sort()

    val pointedToFrameNumber = videoHeader.MobileFrames[seconds]

    var readFrameOffset = 0
    var toReadSize = 0
    for (entry in videoHeader.MobileUniqueFrames) {
        if (entry[0] == pointedToFrameNumber) {
            toReadSize = entry[1]
            break
        } else {
            readFrameOffset += entry[1]
        }
    }

    val currentFrameBytes = buffer.sliceArray(readFrameOffset until readFrameOffset+toReadSize)
    val outputDir = context.cacheDir
    val tmpPngFile = File(outputDir, "tmp_frame.png")
    tmpPngFile.writeBytes(currentFrameBytes)
    return tmpPngFile.toUri()
}