package data.info.models.block.display;

public final class BlockDisplayInfo {
	public final DisplayTransformInfo	firstPerson_rightHand;
	public final DisplayTransformInfo	firstPerson_leftHand;
	public final DisplayTransformInfo	thirdPerson_rightHand;
	public final DisplayTransformInfo	thirdPerson_leftHand;
	public final DisplayTransformInfo	ground;
	public final DisplayTransformInfo	gui;
	public final DisplayTransformInfo	head;
	public final DisplayTransformInfo	fixed;

	public BlockDisplayInfo(DisplayTransformInfo firstPerson_rightHand,
							DisplayTransformInfo firstPerson_leftHand,
							DisplayTransformInfo thirdPerson_rightHand,
							DisplayTransformInfo thirdPerson_leftHand,
							DisplayTransformInfo ground,
							DisplayTransformInfo gui,
							DisplayTransformInfo head,
							DisplayTransformInfo fixed) {
		this.firstPerson_rightHand = firstPerson_rightHand;
		this.firstPerson_leftHand = firstPerson_leftHand;
		this.thirdPerson_rightHand = thirdPerson_rightHand;
		this.thirdPerson_leftHand = thirdPerson_leftHand;
		this.ground = ground;
		this.gui = gui;
		this.head = head;
		this.fixed = fixed;
	}
}
