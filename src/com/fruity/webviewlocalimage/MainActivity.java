package com.fruity.webviewlocalimage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

	public static final FrameLayout.LayoutParams ZOOM_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FrameLayout mContentView = (FrameLayout) getWindow().getDecorView().findViewById(
				android.R.id.content);
		WebView webview = (WebView) findViewById(R.id.mainWebview);
		final View zoom = webview.getZoomControls();
		// For multitouch zoom
		webview.getSettings().setJavaScriptEnabled(false);
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setPluginsEnabled(true);
		// end multitouch zoom
		mContentView.addView(zoom, ZOOM_PARAMS);
		InputStream is;
		ArrayList<String> listUrlImage = new ArrayList<String>();
		try {
			is = getResources().getAssets().open("mon2.html");

			String textfile = convertStreamToString(is);

			Pattern titleFinder = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>",
					Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
			Matcher regexMatcher = titleFinder.matcher(textfile);
			while (regexMatcher.find()) {
				Log.i("==== Image Src ====", regexMatcher.group(1));
				listUrlImage.add(regexMatcher.group(1));
			}

			for (String string : listUrlImage) {
				String fileName = string.substring(string.lastIndexOf("/") + 1, string.length());
				Log.e("Lemon", "File name :" + fileName);
				textfile = textfile.replace(string, "file:///android_asset/" + fileName);
			}

			Log.i("HTML CONTENT ", textfile);

			webview.loadDataWithBaseURL("", textfile, "text/html", "UTF-8", "");
			// webview.loadDataWithBaseURL("file:///android_asset/", textfile,
			// "text/html", "UTF-8", "");
			// webview.loadUrl("file:///android_asset/mon2.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String convertStreamToString(InputStream is) throws IOException {
		Writer writer = new StringWriter();

		char[] buffer = new char[2048];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			is.close();
		}
		String text = writer.toString();
		return text;
	}

}
