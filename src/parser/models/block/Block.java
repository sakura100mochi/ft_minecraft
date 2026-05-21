package parser.models.block;

import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import data.info.Identifier;
import data.info.models.block.BlockInfo;
import data.info.models.block.display.BlockDisplayInfo;
import data.info.models.block.elements.BlockElementsInfo;
import parser.models.block.elements.BlockElements;
import parser.models.block.ambientocclusion.BlockAmbientocclusion;
import parser.models.block.gui_light.BlockGui_Light;
import parser.models.block.display.BlockDisplay;
import utils.registry.Registry;

public final class Block {
	private final Map<Integer, BlockInfo>	allBlockInfo;

	public Block(String blockPath, String texturePath) throws Exception {
		this.allBlockInfo = new HashMap<>();
		makeAllBlockInfo(blockPath, texturePath);
	}

	public BlockInfo getBlockInfo(String name) throws Exception {
		if (name == null) {
			throw new IllegalArgumentException("parser.models.block.getBlockInfo() | Invalid argument");
		}

		Integer id = Registry.getId(name);
		if (id == null) {
			throw new Exception("parser.models.block.getBlockInfo() | " + name + " does not exist");
		}
		BlockInfo blockInfo = allBlockInfo.get(id);
		return blockInfo;
	}

	public BlockInfo getBlockInfo(int id) {
		BlockInfo blockInfo = allBlockInfo.get(id);
		return blockInfo;
	}

	private void makeAllBlockInfo(String blockPath, String texturePath) throws Exception {
		SetRelativeFiles setRelativeFiles = new SetRelativeFiles(blockPath);
		Map<String, Map<String, JSONObject>> relativeFiles = setRelativeFiles.getRelativeFiles();
		DivideFiles divideFiles = new DivideFiles(relativeFiles);
		BlockElements blockElements = new BlockElements(divideFiles.getTextures(), texturePath);
		Map<String, List<BlockElementsInfo>> blockElementsMap = blockElements.get(divideFiles.getElements());
		Map<String, BlockDisplayInfo> display = BlockDisplay.get(divideFiles.getDisplay());
		Map<String, Boolean> ambientocclusion = BlockAmbientocclusion.get(divideFiles.getAmbientocclusion());
		Map<String, String> blockGui_light = BlockGui_Light.get(divideFiles.getGui_light());

		for (String key : relativeFiles.keySet()) {
			String identifier = Identifier.fromRootPath(key);
			int id = Registry.register(identifier);
			BlockInfo blockInfo = new BlockInfo(
				identifier,
				null,
				blockElementsMap.get(key),
				display.get(key),
				ambientocclusion.get(key) != null ? ambientocclusion.get(key) : false,
				blockGui_light.get(key)
			);
			allBlockInfo.put(id, blockInfo);
		}
	}
}
