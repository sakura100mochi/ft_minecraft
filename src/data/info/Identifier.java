package data.info;

public final class Identifier {
	private Identifier() {}

	// ex: return "minecraft:mossy_carpet_side" from 1.21.11/assets/minecraft/models/block/mossy_carpet_side.json
	public static String fromRootPath(String rootPath) throws Exception {
		if (rootPath == null) {
			throw new IllegalArgumentException("data.info.Identifier.fromRootPath() | argument is null.");
		}
		String[] parts = rootPath.split("/");
		if (parts.length < 3 || parts[parts.length - 1].contains(".") == false) {
			throw new IllegalArgumentException("data.info.Identifier.fromRootPath() | argument is not a valid root path.");
		}
		String namespace = parts[2];
		String valueWithExtension = parts[parts.length - 1];
		String value = valueWithExtension.substring(0, valueWithExtension.lastIndexOf('.'));
		return namespace + ":" + value;
	}

	// ex: return "grass_block.json" from ("minecraft:grass_block", ".json")
	public static String getFileNameFromIdentifier(String identifier, String extension) throws Exception {
		if (identifier == null || extension == null) {
			throw new IllegalArgumentException("data.info.Identifier.getFileNameFromIdentifier() | argument is null.");
		}
		if (identifier.contains(":") == false) {
			throw new IllegalArgumentException("data.info.Identifier.getFileNameFromIdentifier() | argument is not a valid identifier.");
		}
		String[] parts = identifier.split(":");
		if (parts.length != 2) {
			throw new IllegalArgumentException("data.info.Identifier.getFileNameFromIdentifier() | argument is not a valid identifier.");
		}
		String value = parts[1];
		return value + extension;
	}

	// ex: return "grass_block" from "minecraft:grass_block"
	public static String getValueFromIdentifier(String identifier) throws Exception {
		if (identifier == null) {
			throw new IllegalArgumentException("data.info.Identifier.getValueFromIdentifier() | argument is null.");
		}
		if (identifier.contains(":") == false) {
			throw new IllegalArgumentException("data.info.Identifier.getValueFromIdentifier() | argument is not a valid identifier.");
		}
		String[] parts = identifier.split(":");
		if (parts.length != 2) {
			throw new IllegalArgumentException("data.info.Identifier.getValueFromIdentifier() | argument is not a valid identifier.");
		}
		return parts[1];
	}

	public static String unknown() {
		return "minecraft:unknown";
	}
}