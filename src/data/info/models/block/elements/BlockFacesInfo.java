package data.info.models.block.elements;

public final class BlockFacesInfo {
	public final Face	east;
	public final Face	west;
	public final Face	south;
	public final Face	north;
	public final Face	up;
	public final Face	down;
	public final Face	particle;

	public BlockFacesInfo(Face east, Face west, Face south, Face north, Face up, Face down, Face particle) {
		this.east = east;
		this.west = west;
		this.south = south;
		this.north = north;
		this.up = up;
		this.down = down;
		this.particle = particle;
	}

	@Override
	public String toString() {
		return "BlockFacesInfo [east=" + east + ",\n\t\t\twest=" + west + ",\n\t\t\tsouth=" + south + ",\n\t\t\tnorth=" + north + ",\n\t\t\tup=" + up + ",\n\t\t\tdown=" + down + ",\n\t\t\tparticle=" + particle + "]";
	}
}
