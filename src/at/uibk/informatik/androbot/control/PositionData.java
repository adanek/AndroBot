package at.uibk.informatik.androbot.control;

import at.uibk.informatik.androbot.contracts.IPositionData;

public class PositionData implements IPositionData {


	private byte xlow;
	private byte xheigh;
	private byte ylow;
	private byte yheigh;
	private byte alphalow;
	private byte alphaheigh;
	
	public PositionData(byte xlow, byte xheigh, byte ylow, byte yheigh,
			byte alphalow, byte alphaheigh) {
		super();
		this.xlow = xlow;
		this.xheigh = xheigh;
		this.ylow = ylow;
		this.yheigh = yheigh;
		this.alphalow = alphalow;
		this.alphaheigh = alphaheigh;
	}
	
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#getXlow()
	 */
	@Override
	public byte getXlow() {
		return xlow;
	}
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#setXlow(byte)
	 */
	@Override
	public void setXlow(byte xlow) {
		this.xlow = xlow;
	}
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#getXheigh()
	 */
	@Override
	public byte getXheigh() {
		return xheigh;
	}
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#setXheigh(byte)
	 */
	@Override
	public void setXheigh(byte xheigh) {
		this.xheigh = xheigh;
	}
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#getYlow()
	 */
	@Override
	public byte getYlow() {
		return ylow;
	}
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#setYlow(byte)
	 */
	@Override
	public void setYlow(byte ylow) {
		this.ylow = ylow;
	}
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#getYheigh()
	 */
	@Override
	public byte getYheigh() {
		return yheigh;
	}
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#setYheigh(byte)
	 */
	@Override
	public void setYheigh(byte yheigh) {
		this.yheigh = yheigh;
	}
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#getAlphalow()
	 */
	@Override
	public byte getAlphalow() {
		return alphalow;
	}
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#setAlphalow(byte)
	 */
	@Override
	public void setAlphalow(byte alphalow) {
		this.alphalow = alphalow;
	}
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#getAlphaheigh()
	 */
	@Override
	public byte getAlphaheigh() {
		return alphaheigh;
	}
	/* (non-Javadoc)
	 * @see at.uibk.informatik.androbot.control.IPositionData#setAlphaheigh(byte)
	 */
	@Override
	public void setAlphaheigh(byte alphaheigh) {
		this.alphaheigh = alphaheigh;
	}

}
