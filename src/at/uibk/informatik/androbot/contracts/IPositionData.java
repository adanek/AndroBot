package at.uibk.informatik.androbot.contracts;

public interface IPositionData {

	/**
	 * @return the xlow
	 */
	public abstract byte getXlow();

	/**
	 * @param xlow the xlow to set
	 */
	public abstract void setXlow(byte xlow);

	/**
	 * @return the xheigh
	 */
	public abstract byte getXheigh();

	/**
	 * @param xheigh the xheigh to set
	 */
	public abstract void setXheigh(byte xheigh);

	/**
	 * @return the ylow
	 */
	public abstract byte getYlow();

	/**
	 * @param ylow the ylow to set
	 */
	public abstract void setYlow(byte ylow);

	/**
	 * @return the yheigh
	 */
	public abstract byte getYheigh();

	/**
	 * @param yheigh the yheigh to set
	 */
	public abstract void setYheigh(byte yheigh);

	/**
	 * @return the alphalow
	 */
	public abstract byte getAlphalow();

	/**
	 * @param alphalow the alphalow to set
	 */
	public abstract void setAlphalow(byte alphalow);

	/**
	 * @return the alphaheigh
	 */
	public abstract byte getAlphaheigh();

	/**
	 * @param alphaheigh the alphaheigh to set
	 */
	public abstract void setAlphaheigh(byte alphaheigh);

}