package com.ldw.downloader.utils;

import java.util.List;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * 获取手机上apk文件信息类，主要是判断是否安装在手机上了，安装的版本比较现有apk版本信息
 */
public class ApkSearchUtils {
	public static int INSTALLED = 0; // 表示已经安装，且跟现在这个apk文件是一个版本
	public static int UNINSTALLED = 1; // 表示未安装
	public static int INSTALLED_UPDATE = 2; // 表示已经安装，版本比现在这个版本要低，可以点击按钮更新

	/*
	 * 判断该应用是否在手机上已经安装过，有以下集中情况出现 1.未安装，这个时候按钮应该是“安装”点击按钮进行安装 2.已安装，按钮显示“已安装”
	 * 可以卸载该应用 3.已安装，但是版本有更新，按钮显示“更新” 点击按钮就安装应用
	 */

	/**
	 * 判断该应用在手机中的安装情况
	 * 
	 * @param pm
	 *            PackageManager
	 * @param packageName
	 *            要判断应用的包名
	 * @param versionCode
	 *            要判断应用的版本号
	 */
	public static int doType(PackageManager pm, String packageName,
			int versionCode) {
		List<PackageInfo> pakageinfos = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo pi : pakageinfos) {
			String pi_packageName = pi.packageName;
			int pi_versionCode = pi.versionCode;
			// 如果这个包名在系统已经安装过的应用中存在
			if (packageName.endsWith(pi_packageName)) {
				// Log.i("test","此应用安装过了");
				if (versionCode == pi_versionCode) {
					Log.i("test", "已经安装，不用更新，可以卸载该应用");
					return INSTALLED;
				} else if (versionCode > pi_versionCode) {
					Log.i("test", "已经安装，有更新");
					return INSTALLED_UPDATE;
				}
			}
		}
		Log.i("test", "未安装该应用，可以安装");
		return UNINSTALLED;
	}

	public static int doType(PackageManager pm, String packageName) {
		List<PackageInfo> pakageinfos = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo pi : pakageinfos) {
			String pi_packageName = pi.packageName;
			// 如果这个包名在系统已经安装过的应用中存在
			if (packageName.endsWith(pi_packageName)) {
				// Log.i("test","此应用安装过了");
				return INSTALLED;
			}
		}
		Log.i("test", "未安装该应用，可以安装");
		return UNINSTALLED;
	}

}
