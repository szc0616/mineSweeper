package kernel;

public class tile extends javax.swing.JLabel {
	private static final long serialVersionUID = -6083351705950803873L;
	private boolean tileFlag = false;
	private boolean editable = true;
	private boolean isBoom = false;
	private int booms;
	public tile() {
		super();
		booms = 0;
	}
	
	public boolean isBoom() {
		return isBoom;
	}

	public void setBoom(boolean isBoom) {
		this.isBoom = isBoom;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isTileFlag() {
		return tileFlag;
	}

	public void setTileFlag(boolean tileFlag) {
		this.tileFlag = tileFlag;
	}

	public int getBooms() {
		return booms;
	}

	public void setBooms(int booms) {
		this.booms = booms;
	}
}
