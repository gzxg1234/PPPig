package com.sanron.pigplayer

/**
 * Author:sanron
 * Time:2019/5/8
 * Description:
 */
object PlayerConst {

    //正常状态
    val STATE_NORMAL = 0

    //准备中
    val STATE_PREPARING = 2

    //播放中
    val STATE_PLAYING = 3

    //暂停
    val STATE_PAUSE = 4

    //播放完成
    val STATE_COMPLETE = 5

    //错误状态
    val STATE_ERROR = 6


    //onInfo
    //未知
    const val MEDIA_INFO_UNKNOWN = 1

    //播放下一条
    val MEDIA_INFO_STARTED_AS_NEXT = 2
    //视频开始整备中
    val MEDIA_INFO_VIDEO_RENDERING_START = 3
    //视频日志跟踪
    val MEDIA_INFO_VIDEO_TRACK_LAGGING = 700
    //缓冲开始
    val MEDIA_INFO_BUFFERING_START = 701
    //缓存结束
    val MEDIA_INFO_BUFFERING_END = 702
    //网络带宽信息
    val MEDIA_INFO_NETWORK_BANDWIDTH = 703
    //
    val MEDIA_INFO_BAD_INTERLEAVING = 800
    //不可设置播放位置
    val MEDIA_INFO_NOT_SEEKABLE = 801
    //元数据
    val MEDIA_INFO_METADATA_UPDATE = 802
    //
    val MEDIA_INFO_TIMED_TEXT_ERROR = 900
    //不支持字母
    val MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901
    //字幕超时
    val MEDIA_INFO_SUBTITLE_TIMED_OUT = 902
    //方向改变
    val MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001
    //准备渲染音频
    val MEDIA_INFO_AUDIO_RENDERING_START = 10002
    //视频解码开始
    val MEDIA_INFO_AUDIO_DECODED_START = 10003
    //音频解码开始
    val MEDIA_INFO_VIDEO_DECODED_START = 10004
    //
    val MEDIA_INFO_OPEN_INPUT = 10005
    //
    val MEDIA_INFO_FIND_STREAM_INFO = 10006
    //
    val MEDIA_INFO_COMPONENT_OPEN = 10007
    //
    val MEDIA_INFO_VIDEO_SEEK_RENDERING_START = 10008
    //
    val MEDIA_INFO_AUDIO_SEEK_RENDERING_START = 10009
    //定位完成
    val MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE = 10100

    //onError
    //未知错误
    val MEDIA_ERROR_UNKNOWN = 1
    //服务崩溃
    val MEDIA_ERROR_SERVER_DIED = 100
    //
    val MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200
    //IO错误
    val MEDIA_ERROR_IO = -1004
    //
    val MEDIA_ERROR_MALFORMED = -1007
    //不支持播放
    val MEDIA_ERROR_UNSUPPORTED = -1010
    //超时
    val MEDIA_ERROR_TIMED_OUT = -110
}
