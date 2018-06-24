package jp.fukui.controllergenerator.fxml;

import java.io.*;

/** コントローラ生成クラス */
public class ControllerGenerator {
	/** コントローラの生成 */
	public void output(File dst, ControllerHint hint, boolean useEventObserver) {
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(dst));

			String controllerPkg = hint.getControllerPkg();
			if(controllerPkg != null) {
				out.write("package ");
				out.write(controllerPkg);
				out.write(";\n");
			}

			out.write("\n");

			out.write("import java.net.URL;\n");
			out.write("import java.util.ResourceBundle;\n");
			out.write("import javafx.fxml.FXML;\n");

			out.write("import javafx.fxml.Initializable;\n");
			for(String path : hint.usedClassList) {
				out.write("import ");
				out.write(path);
				out.write(";\n");
			}

			out.write("\n");

			out.write("public class ");
			out.write(hint.getControllerName());
			out.write(" implements Initializable");
			out.write(" {\n");

			out.write("\t@Override public void initialize(URL location, ResourceBundle resources) { }\n");

			if(hint.idClassMap.isEmpty() == false) {
				out.write("\n");
				for(String id : hint.idClassMap.keySet()) {
					out.write("\t@FXML private ");
					out.write(hint.idClassMap.get(id));
					out.write(" ");
					out.write(id);
					out.write(";\n");
				}

				out.write("\n");
				for(String id : hint.idClassMap.keySet()) {
					out.write("\tpublic ");
					out.write(hint.idClassMap.get(id));
					out.write(" get");
					out.write(Character.toUpperCase(id.charAt(0)));
					out.write(id.substring(1));
					out.write("() { return(this.");
					out.write(id);
					out.write("); }\n");
				}
			}

			if(hint.hndlList.isEmpty() == false) {
				out.write("\n");
				for(String[] h : hint.hndlList) {
					out.write("\t@FXML public void ");
					out.write(h[0]);
					out.write("(");
					out.write(h[1]);
					out.write(" e) {");
					if(useEventObserver) {
						out.write(" this._o.");
						out.write(h[0]);
						out.write("(e); ");
					}
					out.write(" }\n");
				}
			}

			if(useEventObserver) {
				out.write("\n");
				out.write("\t//------------------------------------------------------------------------------\n");
				out.write("\t// Event notification\n");
				out.write("\t//------------------------------------------------------------------------------\n");
				out.write("\n");
				out.write("\tpublic interface Observer {\n");
				for(String[] h : hint.hndlList) {
					out.write("\t\tpublic void ");
					out.write(h[0]);
					out.write("(");
					out.write(h[1]);
					out.write(" e);\n");
				}
				out.write("\t}\n");
				out.write("\n");
				out.write("\tprivate Observer _o = null;\n");
				out.write("\tpublic void setObserver(Observer o) { this._o = o; }\n");
			}

			out.write("}\n");

			out.close();
			out = null;
		}
		catch(Exception ex) {
			if(out != null)
				try { out.close(); } catch(Exception _ex) {}
			ex.printStackTrace();
		}

		return;
	}
}
