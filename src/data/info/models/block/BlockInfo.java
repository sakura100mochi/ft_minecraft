package data.info.models.block;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import data.info.Identifier;
import data.info.models.block.display.BlockDisplayInfo;
import data.info.models.block.display.DisplayTransformInfo;
import data.info.models.block.elements.BlockElementsInfo;
import data.info.models.block.elements.BlockFacesInfo;
import data.info.models.block.elements.BlockRotationInfo;
import data.info.models.block.elements.Face;

public final class BlockInfo {
	public final String					identifier;
	public final JSONArray				properties;
	public final List<BlockElementsInfo>	elements;
	public final BlockDisplayInfo		display;
	public final boolean				ambientocclusion;
	public final String					gui_light;
	public final static BlockInfo		waterInfo;
	public static int					maxElementCount = 0;

	static {
		try {
			waterInfo = getWaterInfo();
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize waterInfo", e);
		}
	}

	public BlockInfo(String identifier, JSONArray properties, List<BlockElementsInfo> elements,
					BlockDisplayInfo display, boolean ambientocclusion, String gui_light) throws Exception {
		if (identifier == null) {
			identifier = Identifier.unknown();
		}
		if (elements == null) {
			String[] textures = new String[7];
			textures[BlockElementsInfo.TextureEnum.East.ordinal()] = "debug2.png";
			textures[BlockElementsInfo.TextureEnum.West.ordinal()] = "debug2.png";
			textures[BlockElementsInfo.TextureEnum.South.ordinal()] = "debug2.png";
			textures[BlockElementsInfo.TextureEnum.North.ordinal()] = "debug2.png";
			textures[BlockElementsInfo.TextureEnum.Up.ordinal()] = "debug2.png";
			textures[BlockElementsInfo.TextureEnum.Down.ordinal()] = "debug2.png";
			textures[BlockElementsInfo.TextureEnum.Particle.ordinal()] = "debug2.png";

			elements = new ArrayList<>();
			elements.add(new BlockElementsInfo(0, 0, 0,
											16, 16, 16,
											new BlockFacesInfo(
												new Face("#east", null, null, 0),
												new Face("#west", null, null, 0),
												new Face("#south", null, null, 0),
												new Face("#north", null, null, 0),
												new Face("#up", null, null, 0),
												new Face("#down", null, null, 0),
												new Face("#particle", null, null, 0)
											),
											new BlockRotationInfo(0, 0, 0, "x", 0, false),
											false, "unknown",
											textures));
		}
		if (display == null) {
			display = new BlockDisplayInfo(
						new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
						new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
						new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
						new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
						new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
						new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
						new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
						new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0)
			);
		}
		if (gui_light == null) {
			gui_light = "unknown";
		}
		this.identifier = identifier;
		this.properties = properties;
		this.elements = elements;
		this.display = display;
		this.ambientocclusion = ambientocclusion;
		this.gui_light = gui_light;

		if (elements.size() > maxElementCount) {
			maxElementCount = elements.size();
		}
	}

	private static BlockInfo getWaterInfo() throws Exception {
		String[] textures = new String[7];
		textures[BlockElementsInfo.TextureEnum.East.ordinal()] = "water_flow.png";
		textures[BlockElementsInfo.TextureEnum.West.ordinal()] = "water_flow.png";
		textures[BlockElementsInfo.TextureEnum.South.ordinal()] = "water_flow.png";
		textures[BlockElementsInfo.TextureEnum.North.ordinal()] = "water_flow.png";
		textures[BlockElementsInfo.TextureEnum.Up.ordinal()] = "water_still.png";
		textures[BlockElementsInfo.TextureEnum.Down.ordinal()] = "water_still.png";
		textures[BlockElementsInfo.TextureEnum.Particle.ordinal()] = "water_still.png";

		List<BlockElementsInfo> elements = new ArrayList<>();
		elements.add(new BlockElementsInfo(0, 0, 0,
									16, 16, 16,
									new BlockFacesInfo(
										new Face("#east", null, null, 0),
										new Face("#west", null, null, 0),
										new Face("#south", null, null, 0),
										new Face("#north", null, null, 0),
										new Face("#up", null, null, 0),
										new Face("#down", null, null, 0),
										new Face("#particle", null, null, 0)
									),
									new BlockRotationInfo(0, 0, 0, "x", 0, false),
									false, "unknown",
									textures));
		return new BlockInfo("minecraft:water",
							null,
							elements,
							new BlockDisplayInfo(
										new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
										new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
										new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
										new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
										new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
										new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
										new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0),
										new DisplayTransformInfo(0, 0, 0, 0, 0, 0, 0, 0, 0)
			),
							false, "unknown");
	}

	@Override
	public String toString() {
		return "BlockInfo(identifier=" + identifier + ",\n\telements=" + elements +
				",\n\tdisplay=" + display + ",\n\tambientocclusion=" + ambientocclusion + ",\n\tgui_light=" + gui_light + ")";
	}
}