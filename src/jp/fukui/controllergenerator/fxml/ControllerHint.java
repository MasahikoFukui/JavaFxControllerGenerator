package jp.fukui.controllergenerator.fxml;

import java.io.*;
import java.util.*;

/** コントローラ生成時のヒント情報 */
public class ControllerHint {
	public String               controllerClass = null;
	public ArrayList<String>    importList      = null;
	public ArrayList<String>    usedClassList   = null;
	public Map<String, String>  idClassMap      = null;
	public ArrayList<String[]>  hndlList        = null;

	public String getControllerPkg() {
		int idx = this.controllerClass.lastIndexOf('.');
		String pkg = null;
		if(idx != -1)
			pkg = this.controllerClass.substring(0, idx);

		return(pkg);
	}

	public String getControllerName() {
		int idx = this.controllerClass.lastIndexOf('.');
		String name = this.controllerClass.substring(idx + 1);
		return(name);
	}

	public String getControllerDirectory() {
		int idx = this.controllerClass.lastIndexOf('.');
		if(idx == -1)
			idx = this.controllerClass.length();
		return(classToFile(this.controllerClass.substring(0, idx)));
	}

	public File getControllerFile() {
		return(new File(classToFile(this.controllerClass) + ".java"));
	}

	private String classToFile(String classPath) {
		StringBuffer path = new StringBuffer(classPath);
		for(int i = 0; i < path.length(); i++) {
			switch(path.charAt(i)) {
			case '\\'	:	i++;						break;
			case '.'	:	path.replace(i, i+1, "/");	break;
			}
		}
		return(path.toString());
	}
}
