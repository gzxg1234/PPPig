package com.sanron.pppig.module.play


import com.sanron.pppig.util.limit
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager

class PigVideoManager : IjkPlayerManager() {

    override fun seekTo(time: Long) {
        //修复seek到最大值时，一直loading的问题
        super.seekTo(time.limit(0L, (duration - 1).limit(min = 0L)))
    }
}