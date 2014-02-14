package com.ldw.downloader.test;

import java.net.MalformedURLException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldw.downloader.R;
import com.ldw.downloader.db.DownloadDao;
import com.ldw.downloader.service.ServiceManager;
import com.ldw.downloader.utils.ApkSearchUtils;
import com.ldw.downloader.utils.DownloadConstants;
import com.ldw.downloader.utils.MyIntents;
import com.ldw.downloader.utils.StorageUtils;

public class MainActivity extends Activity {

	private String[] name = { "网易新闻", "美团", "天天动听", "嘉城广告" };
	private String[] packageName = { "com.netease.newsreader.activity",
			"com.sankuai.meituan", "com.sds.android.ttpod", "com.bugull.jc" };
	private int[] icon = { R.drawable.wangyi_icon, R.drawable.meituan_icon,
			R.drawable.ttpod_icon, R.drawable.jiacheng_icon };
	private String[] url = { "http://test.longdw.com/wangyi.apk",
			"http://test.longdw.com/meituan.apk",
			"http://test.longdw.com/tiantiandongting.apk",
			"http://test.longdw.com/jc-1.1.1.apk" };

	private ArrayList<APK> list = new ArrayList<APK>();
	private PackageManager pm;
	private DownloadDao mDao;
	private MyReceiver mReceiver;

	private ListView mListView;
	private MyAdapter mAdapter;

	private ServiceManager mServiceManager;
	private com.ldw.downloader.test.MainActivity.InstallReceiver mInstallReceiver;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mServiceManager = ServiceManager.getInstance(this);
		pm = getPackageManager();
		mDao = new DownloadDao(this);

		mReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadConstants.RECEIVER_ACTION);
		registerReceiver(mReceiver, filter);
		
		mInstallReceiver = new InstallReceiver();
		IntentFilter installFilter = new IntentFilter();
		installFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		installFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		installFilter.addDataScheme("package");
		registerReceiver(mInstallReceiver, installFilter);

		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		
		try {
			initData();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void initData() throws MalformedURLException {
		list.clear();
		for (int i = 0; i < 4; i++) {
			APK apk = new APK();
			apk.setIcon(icon[i]);
			apk.setName(name[i]);
			apk.setPackageName(packageName[i]);
			apk.setUrl(url[i]);

			int status = mDao.getStatusByUrl(url[i]);
			if (status != DownloadConstants.STATUS_DEFAULT) {
				apk.setStatus(status);
			} else {
				if (ApkSearchUtils.doType(pm, packageName[i]) == ApkSearchUtils.INSTALLED) {
					apk.setStatus(DownloadConstants.STATUS_COMPLETE);
				} else {
					apk.setStatus(DownloadConstants.STATUS_DEFAULT);
				}
			}

			// URL ur = new URL(url[i]);
			// String fileName = new File(ur.getFile()).getName();
			// File file = new File(StorageUtils.FILE_ROOT, fileName);
			// if(ApkSearchUtils.doType(pm, packageName[i]) ==
			// ApkSearchUtils.INSTALLED) {
			// apk.setStatus(3);
			// } else if(file.exists() && ApkSearchUtils.doType(pm,
			// packageName[i]) == ApkSearchUtils.UNINSTALLED) {
			// apk.setStatus(2);
			// } else {
			// apk.setStatus(0);
			// }
			list.add(apk);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mServiceManager.disConnectService();
		unregisterReceiver(mReceiver);
	}
	
	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (intent != null
					&& intent.getAction().equals(
							DownloadConstants.RECEIVER_ACTION)) {
				
				int type = intent.getIntExtra(MyIntents.TYPE, -1);
				switch (type) {
				case MyIntents.Types.WAIT: {// 下载之前的等待
					String url = intent.getStringExtra(MyIntents.URL);
					View itemView = mListView.findViewWithTag(url);
					if (itemView != null) {
						Button btn = (Button) itemView
								.findViewById(R.id.item_btn);
						btn.setClickable(true);
						btn.setText("等待");
						for (APK apk : list) {
							if (apk.getUrl().equals(url)) {
								apk.setStatus(DownloadConstants.STATUS_DOWNLOADING);
								mAdapter.notifyDataSetChanged();
								break;
							}
						}
					}

				}
				break;
				case MyIntents.Types.PROCESS: {
					String url = intent.getStringExtra(MyIntents.URL);
					View itemView = mListView.findViewWithTag(url);
					if (itemView != null) {
						String progress = intent
								.getStringExtra(MyIntents.PROCESS_PROGRESS);
						Button btn = (Button) itemView
								.findViewById(R.id.item_btn);
						btn.setText(progress + "%");
					}
				}
				break;
				case MyIntents.Types.COMPLETE: {
					String url = intent.getStringExtra(MyIntents.URL);
					for (APK apk : list) {
						if (apk.getUrl().equals(url)) {
							apk.setStatus(DownloadConstants.STATUS_INSTALL);
							mAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
				break;
				case MyIntents.Types.ERROR:
					String url = intent.getStringExtra(MyIntents.URL);
					String errorMsg = intent
							.getStringExtra(MyIntents.ERROR_INFO);
					if (!TextUtils.isEmpty(errorMsg)) {
						System.out.println(errorMsg);
						Toast.makeText(getApplicationContext(), errorMsg,
								Toast.LENGTH_SHORT).show();
					}
					for (APK apk : list) {
						if (apk.getUrl().equals(url)) {
							apk.setStatus(DownloadConstants.STATUS_PAUSE);
							mAdapter.notifyDataSetChanged();
							break;
						}
					}
					break;
				}
			}
		}
	}

	private class MyAdapter extends BaseAdapter {

		private Drawable downloadImg, pauseImg, openImg, resumeImg, installImg;

		public MyAdapter() {
			downloadImg = getResources().getDrawable(R.drawable.download);
			downloadImg.setBounds(0, 0, downloadImg.getMinimumWidth(),
					downloadImg.getMinimumHeight());

			pauseImg = getResources().getDrawable(R.drawable.pause);
			pauseImg.setBounds(0, 0, pauseImg.getMinimumWidth(),
					pauseImg.getMinimumHeight());

			openImg = getResources().getDrawable(R.drawable.open);
			openImg.setBounds(0, 0, openImg.getMinimumWidth(),
					openImg.getMinimumHeight());

			resumeImg = getResources().getDrawable(R.drawable.resume);
			resumeImg.setBounds(0, 0, resumeImg.getMinimumWidth(),
					resumeImg.getMinimumHeight());

			installImg = getResources().getDrawable(R.drawable.install);
			installImg.setBounds(0, 0, installImg.getMinimumWidth(),
					installImg.getMinimumHeight());
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public APK getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			// ViewHolder holder;
			final APK apk = getItem(position);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.listview_item, null);
			}
			// else {
			// holder = (ViewHolder) convertView.getTag(1);
			// }
			// holder = new ViewHolder();
			TextView nameTv = (TextView) convertView
					.findViewById(R.id.item_name);
			ImageView iv = (ImageView) convertView.findViewById(R.id.item_iv);
			final Button btn = (Button) convertView.findViewById(R.id.item_btn);
			convertView.setTag(apk.getUrl());

			iv.setImageResource(apk.getIcon());
			nameTv.setText(apk.getName());

			if (apk.getStatus() == DownloadConstants.STATUS_COMPLETE) {
				btn.setCompoundDrawables(null, openImg, null, null);
				btn.setText("打开");
			} else if (apk.getStatus() == DownloadConstants.STATUS_INSTALL) {
				btn.setCompoundDrawables(null, installImg, null, null);
				btn.setText("安装");
			} else if (apk.getStatus() == DownloadConstants.STATUS_DOWNLOADING) {
				btn.setCompoundDrawables(null, pauseImg, null, null);
//				btn.setText("正在下载");
			} else if (apk.getStatus() == DownloadConstants.STATUS_PAUSE) {
				btn.setCompoundDrawables(null, resumeImg, null, null);
				btn.setText("继续");
			} else if (apk.getStatus() == DownloadConstants.STATUS_DEFAULT) {
				btn.setCompoundDrawables(null, downloadImg, null, null);
				btn.setText("下载");
			}

			btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					if (apk.getStatus() == DownloadConstants.STATUS_COMPLETE) {

						StorageUtils.openAPK(pm, apk.getPackageName(),
								MainActivity.this);

					} else if (apk.getStatus() == DownloadConstants.STATUS_INSTALL) {

						StorageUtils.installAPK(MainActivity.this, apk.getUrl());

					} else if (apk.getStatus() == DownloadConstants.STATUS_DOWNLOADING) {//暂停

						mServiceManager.pauseTask(apk.getUrl());
						apk.setStatus(DownloadConstants.STATUS_PAUSE);
						notifyDataSetChanged();

					} else if (apk.getStatus() == DownloadConstants.STATUS_PAUSE) {//继续下载

						mServiceManager.continueTask(apk.getUrl());
						apk.setStatus(DownloadConstants.STATUS_DOWNLOADING);
						notifyDataSetChanged();

					} else if (apk.getStatus() == DownloadConstants.STATUS_DEFAULT) {//默认 需要下载

						mServiceManager.addTask(apk.getUrl());
						btn.setClickable(false);
						
					}
				}
			});

			return convertView;
		}

//		public class ViewHolder {
//			TextView nameTv, proTv;
//			ImageView iv;
//			Button btn;
//		}
	}
	
	public class InstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String data = intent.getDataString();
			if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {// 有应用被添加
				if (data != null) {
					String pack = data.split(":")[1];
					for (APK apk : list) {
						if (apk.getPackageName().equals(pack)) {
							mDao.deleteByUrl(apk.getUrl());
							apk.setStatus(DownloadConstants.STATUS_COMPLETE);
							mAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			} else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {// 有应用被卸载
				try {
					initData();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

}
