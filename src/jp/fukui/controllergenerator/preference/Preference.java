package jp.fukui.controllergenerator.preference;

import java.io.*;
import java.util.*;

/**
 * 初期設定
 */
public class Preference {
	//------------------------------------------------------------------------------
	// 生成と破棄
	//------------------------------------------------------------------------------

	public Preference() {
		this.prop = new Properties();
		loadProperties();
	}

	public void close() {
		saveProperties();
	}

	//------------------------------------------------------------------------------
	// プロパティ
	//------------------------------------------------------------------------------

	public void    setDirectory(File dir)         { setFile("directory", dir); }
	public File    getDirectory()                 { return(getFile("directory")); }
	public void    setUseEventObserver(boolean b) { setBool("useEventObserver", b); }
	public boolean isUseEventObserver()           { return(getBool("useEventObserver", false)); }
	public void    setOpenWhole(boolean b)        { setBool("openWhole", b); }
	public boolean isOpenWhole()                  { return(getBool("openWhole", true)); }

	public void setFile(String key, File f) {
		String path = "";
		if(f != null)
			path = f.getPath();
		setProperty(key, path);
	}

	public File getFile(String key) {
		String path = getProperty(key);
		if(path == null || path.isEmpty())
			return(null);
		return(new File(path));
	}

	public void setInt(String key, int n) {
		setProperty(key, Integer.toString(n));
	}

	public int getInt(String key, int defaultVal) {
		String s = getProperty(key);
		if(s == null || s.isEmpty())
			return(defaultVal);
		return(Integer.parseInt(s));
	}

	public void setBool(String key, boolean b) {
		setProperty(key, Boolean.toString(b));
	}

	public boolean getBool(String key, boolean defaultVal) {
		String s = getProperty(key);
		if(s == null || s.isEmpty())
			return(defaultVal);
		return(Boolean.parseBoolean(s));
	}

	//------------------------------------------------------------------------------
	// プロパティファイル参照
	//------------------------------------------------------------------------------

	private Properties prop;

	private String getProperty(String key) {
		return(this.prop.getProperty(key));
	}

	private void setProperty(String key, String val) {
		this.prop.setProperty(key, val);
	}

	private void loadProperties() {
		InputStream in = null;
		try {
			in = new FileInputStream("app.properties");
			this.prop.load(in);
		}
		catch(Exception ex) {
		}
		finally {
			try { in.close(); } catch(Exception ex) { }
		}
	}

	private void saveProperties() {
		OutputStream out = null;
		try {
			out = new FileOutputStream("app.properties");
			this.prop.store(out, "Application preference");
		}
		catch(Exception ex) {
		}
		finally {
			try { out.close(); } catch(Exception ex) { }
		}
	}
}
