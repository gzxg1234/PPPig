package com.sanron.pppig.util

import android.graphics.Bitmap
import android.net.Uri
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequestBuilder

/**
 * Author:sanron
 * Time:2019/4/25
 * Description:
 */
object FrescoUtil {
    fun getBitmap(url: String, callback: (Bitmap?) -> Unit) {
        val requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
        val imageRequest = requestBuilder.build()
        val dataSource =
                ImagePipelineFactory.getInstance().imagePipeline.fetchDecodedImage(imageRequest, null)
        dataSource.subscribe(object : BaseBitmapDataSubscriber() {
            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {
            }
            override fun onNewResultImpl(bitmap: Bitmap?) {
                callback(bitmap)
            }
        }, UiThreadImmediateExecutorService.getInstance())

    }
}
