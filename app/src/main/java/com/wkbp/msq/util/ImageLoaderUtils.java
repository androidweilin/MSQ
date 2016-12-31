package com.wkbp.msq.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.wkbp.msq.R;

import java.io.File;

public class ImageLoaderUtils {
	/**
	 * 初始化imageLoader
	 */
	public static void init(Context context) {
		File cacheDirFile = new File(Constant.APP_PATH + "cacheDir/");
		if (!cacheDirFile.exists()) {
			cacheDirFile.mkdirs();
		}
		// File cacheDir = StorageUtils.getCacheDirectory(instance); //缓存文件夹路径
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				// .showStubImage(R.drawable.default_avatar)//缓冲过程中图片
				.showImageForEmptyUri(R.drawable.msq_logo)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.msq_logo)// 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true)// 缓存道内存
				.cacheOnDisc(true)// 缓存到硬盘
				.bitmapConfig(Bitmap.Config.ARGB_8888) // 设置图片的解码类型
				.displayer(new RoundedBitmapDisplayer(20))// 设置形状:new
				// Displayer(20)圆角，不设置为直角
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.memoryCacheExtraOptions(480, 800) // default = device screen
				.diskCacheExtraOptions(480, 800, null) // 本地缓存的详细信息(缓存的最大长宽)，最好不要设置这个
				.threadPoolSize(5) // default 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2) // default 设置当前线程的优先级
				.denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(2 * 1024 * 1024)) // 可以通过自己的内存缓存实现
				.memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
				.memoryCacheSizePercentage(13) // default
				//.diskCache(new UnlimitedDiskCache(cacheDirFile)) // default
				.tasksProcessingOrder(QueueProcessingType.LIFO) // 可以自定义缓存路径
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb sd卡(本地)缓存的最大值
				.diskCacheFileCount(300) // 可以缓存的文件数量
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.imageDownloader(new BaseImageDownloader(context)) // default
				.imageDecoder(new BaseImageDecoder(false)) // default
				.defaultDisplayImageOptions(options) // DisplayImageOptions.createSimple())
													// // default
				//.discCache(new LimitedAgeDiskCache(cacheDirFile, 7 * 24 * 60 * 60))// 自定义缓存路径,7天后自动清除缓存
				.writeDebugLogs() // 打印debug log
				.build(); // 开始构建
		ImageLoader.getInstance().init(config); // 初始化
	}

}
