package worldgen.overworld.biome;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import data.Data;
import settings.SystemSettings;
import utils.math.Position3D;
import utils.math.noise.INoise;
import worldgen.overworld.noiseRouter.NoiseRouter;
import worldgen.overworld.biome.biomeBuilder.BiomeBuilder;
import worldgen.overworld.biome.biomeBuilder.ContinentalnessBuilder.ContinentalnessLevel;
import worldgen.overworld.biome.biomeBuilder.PVBuilder.PVLevel;

public final class Biome {
	private final Map<Long, String> biome_cache = new ConcurrentHashMap<>();
	private final INoise	temperature;
	private final INoise	humidity;
	private final INoise	continentalness;
	private final INoise	erosion;
	private final INoise	depth;
	private final INoise	weirdness;
	public final BiomeBuilder biomeBuilder;
	private final int		min_y;
	private final int		terrainHeight;
	
	public Biome(Data data, NoiseRouter noiseRouter) throws Exception {
		if (data == null || data.parser == null || data.parser.worldgen == null ||
			noiseRouter == null) {
			throw new IllegalArgumentException("worldgen.biome | Invalid argument");
		}

		this.temperature = noiseRouter.temperature;
		this.humidity = noiseRouter.vegetation;
		this.continentalness = noiseRouter.continents;
		this.erosion = noiseRouter.erosion;
		this.depth = noiseRouter.depth;
		this.weirdness = noiseRouter.ridges;
		this.biomeBuilder = new BiomeBuilder();
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
	}

	public String getBiome(int x, int y, int z) {
		int grid_x = (x >> 2) * 4;
		int grid_y = (y >> 2) * 4;
		int grid_z = (z >> 2) * 4;
		long key = Position3D.toLong(grid_x, grid_y, grid_z);
		return this.biome_cache.computeIfAbsent(key, k -> {
			try {
				return generateBiome(grid_x, grid_y, grid_z);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	public int getCacheSize() {
		return this.biome_cache.size();
	}

	public void cleanCache(int chunk_x, int chunk_z) {
		for (int x = chunk_x * SystemSettings.CHUNK_SIZE; x < (chunk_x + 1) * SystemSettings.CHUNK_SIZE; x += 4) {
			for (int y = (this.min_y >> 2) * 4; y < ((this.min_y + this.terrainHeight) >> 2) * 4; y += 4) {
				for (int z = chunk_z * SystemSettings.CHUNK_SIZE; z < (chunk_z + 1) * SystemSettings.CHUNK_SIZE; z += 4) {
					long key = Position3D.toLong(x, y, z);
					this.biome_cache.remove(key);
				}
			}
		}
	}

	private String generateBiome(int grid_x, int grid_y, int grid_z) throws Exception {
		double temperatureValue = this.temperature.sample3D(grid_x, grid_y, grid_z);
		double continentalnessValue = this.continentalness.sample3D(grid_x, grid_y, grid_z);
		double humidityValue = this.humidity.sample3D(grid_x, grid_y, grid_z);
		double erosionValue = this.erosion.sample3D(grid_x, grid_y, grid_z);
		double depthValue = this.depth.sample3D(grid_x, grid_y, grid_z);
		double weirdnessValue = this.weirdness.sample3D(grid_x, grid_y, grid_z);

		int temperatureLevel = this.biomeBuilder.temperatureBuilder.getLevel(temperatureValue);
		ContinentalnessLevel continentalnessLevel = this.biomeBuilder.continentalnessBuilder.getLevel(continentalnessValue);
		int humidityLevel = this.biomeBuilder.humidityBuilder.getLevel(humidityValue);
		int erosionLevel = this.biomeBuilder.erosionBuilder.getLevel(erosionValue);
		PVLevel pvLevel = this.biomeBuilder.pvBuilder.getLevel(weirdnessValue);

		String closest = null;
		double closestDistance = Double.MAX_VALUE;

		if (0.2 <= depthValue && depthValue <= 0.9) {
			if (0.8 <= continentalnessValue && continentalnessValue <= 1.0) {
				return "minecraft:dripstone_caves";
			}
			if (0.7 <= humidityValue && humidityValue <= 1.0) {
				return "minecraft:lush_caves";
			}
		}  else {
			double distance = distanceToRange(depthValue, 0.2, 0.9) +
							distanceToRange(continentalnessValue, 0.8, 1.0);
			if (distance < closestDistance) {
				closestDistance = distance;
				closest = "minecraft:dripstone_caves";
			}
			distance = distanceToRange(depthValue, 0.2, 0.9) +
							distanceToRange(humidityValue, 0.7, 1.0);
			if (distance < closestDistance) {
				closestDistance = distance;
				closest = "minecraft:lush_caves";
			}
		}
		if (depthValue == 1.1) {
			if (-1.0 <= erosionValue && erosionValue <= -0.375) {
				return "minecraft:deep_dark";
			}
		} else {
			double distance = distanceToRange(depthValue, 1.1, 1.1) +
							distanceToRange(erosionValue, -1.0, -0.375);
			if (distance < closestDistance) {
				closestDistance = distance;
				closest = "minecraft:deep_dark";
			}
		}

		if (continentalnessLevel == ContinentalnessLevel.Mushroom_fields) {
			return "minecraft:mushroom_fields";
		}
		if (temperatureLevel == 0) {
			if (continentalnessLevel == ContinentalnessLevel.Deep_ocean) {
				return "minecraft:deep_frozen_ocean";
			}
			if (continentalnessLevel == ContinentalnessLevel.Ocean) {
				return "minecraft:frozen_ocean";
			}
		}
		if (temperatureLevel == 1) {
			if (continentalnessLevel == ContinentalnessLevel.Deep_ocean) {
				return "minecraft:deep_cold_ocean";
			}
			if (continentalnessLevel == ContinentalnessLevel.Ocean) {
				return "minecraft:cold_ocean";
			}
		}
		if (temperatureLevel == 2) {
			if (continentalnessLevel == ContinentalnessLevel.Deep_ocean) {
				return "minecraft:deep_ocean";
			}
			if (continentalnessLevel == ContinentalnessLevel.Ocean) {
				return "minecraft:ocean";
			}
		}
		if (temperatureLevel == 3) {
			if (continentalnessLevel == ContinentalnessLevel.Deep_ocean) {
				return "minecraft:deep_lukewarm_ocean";
			}
			if (continentalnessLevel == ContinentalnessLevel.Ocean) {
				return "minecraft:lukewarm_ocean";
			}
		}
		if (temperatureLevel == 4) {
			return "minecraft:warm_ocean";
		}

		if (erosionLevel == 0) {
			if (pvLevel == PVLevel.Valleys) {
				if (continentalnessLevel == ContinentalnessLevel.Coast ||
					continentalnessLevel == ContinentalnessLevel.Near_inland) {
					if (temperatureLevel == 0) {
						return "minecraft:frozen_river";
					} else {
						return "minecraft:river";
					}
				} else {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, true);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, true);
					}
				}
			} else if (pvLevel == PVLevel.Low) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					return "minecraft:stony_shore";
				} else if (continentalnessLevel == ContinentalnessLevel.Near_inland) {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				} else {
					if (temperatureLevel == 0) {
						if (humidityLevel == 0 || humidityLevel == 1) {
							return "minecraft:snowy_slopes";
						} else {
							return "minecraft:grove";
						}
					} else if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				}
			} else if (pvLevel == PVLevel.Mid) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					return "minecraft:stony_shore";
				} else {
					if (temperatureLevel == 3 || temperatureLevel == 4) {
						return getPlateau(humidityLevel, temperatureLevel, weirdnessValue, false);
					} else {
						if (humidityLevel == 0 || humidityLevel == 1) {
							return "minecraft:snowy_slopes";
						} else {
							return "minecraft:grove";
						}
					}
				}
			} else if (pvLevel == PVLevel.High) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
				} else if (continentalnessLevel == ContinentalnessLevel.Near_inland) {
					if (temperatureLevel == 3 || temperatureLevel == 4) {
						return getPlateau(humidityLevel, temperatureLevel, weirdnessValue, false);
					} else {
						if (humidityLevel == 0 || humidityLevel == 1) {
							return "minecraft:snowy_slopes";
						} else {
							return "minecraft:grove";
						}
					}
				} else {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else if (temperatureLevel == 3) {
						return "minecraft:stony_peaks";
					} else {
						if (weirdnessValue < 0.0) {
							return "minecraft:jagged_peaks";
						} else {
							return "minecraft:frozen_peaks";
						}
					}
				}
			} else {
				if (temperatureLevel == 4) {
					return getBadland(humidityLevel, weirdnessValue, false);
				} else if (temperatureLevel == 3) {
					return "minecraft:stony_peaks";
				} else {
					if (weirdnessValue < 0.0) {
						return "minecraft:jagged_peaks";
					} else {
						return "minecraft:frozen_peaks";
					}
				}
			}
		} else if (erosionLevel == 1) {
			if (pvLevel == PVLevel.Valleys) {
				if (continentalnessLevel == ContinentalnessLevel.Coast ||
					continentalnessLevel == ContinentalnessLevel.Near_inland) {
					if (temperatureLevel == 0) {
						return "minecraft:frozen_river";
					} else {
						return "minecraft:river";
					}
				} else {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, true);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, true);
					}
				}
			} else if (pvLevel == PVLevel.Low) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					return "minecraft:stony_shore";
				} else if (continentalnessLevel == ContinentalnessLevel.Near_inland) {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				} else {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else if (temperatureLevel == 0) {
						if (humidityLevel == 0 || humidityLevel == 1) {
							return "minecraft:snowy_slopes";
						} else {
							return "minecraft:grove";
						}
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				}
			} else if (pvLevel == PVLevel.Mid) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					return "minecraft:stony_shore";
				} else if (continentalnessLevel == ContinentalnessLevel.Near_inland ||
						continentalnessLevel == ContinentalnessLevel.Mid_inland) {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else if (temperatureLevel == 0) {
						if (humidityLevel == 0 || humidityLevel == 1) {
							return "minecraft:snowy_slopes";
						} else {
							return "minecraft:grove";
						}
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				} else {
					if (temperatureLevel == 0) {
						if (humidityLevel == 0 || humidityLevel == 1) {
							return "minecraft:snowy_slopes";
						} else {
							return "minecraft:grove";
						}
					} else {
						return getPlateau(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				}
			} else if (pvLevel == PVLevel.High) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
				} else if (continentalnessLevel == ContinentalnessLevel.Near_inland) {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else if (temperatureLevel == 0) {
						if (humidityLevel == 0 || humidityLevel == 1) {
							return "minecraft:snowy_slopes";
						} else {
							return "minecraft:grove";
						}
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				} else {
					if (temperatureLevel == 3 || temperatureLevel == 4) {
						return getPlateau(humidityLevel, temperatureLevel, weirdnessValue, false);
					} else {
						if (humidityValue == 0 || humidityValue == 1) {
							return "minecraft:snowy_slopes";
						} else {
							return "minecraft:grove";
						}
					}
				}
			} else if (pvLevel == PVLevel.Peaks) {
				if (continentalnessLevel == ContinentalnessLevel.Coast ||
					continentalnessLevel == ContinentalnessLevel.Near_inland) {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else if (temperatureLevel == 0) {
						if (humidityLevel == 0 || humidityLevel == 1) {
							return "minecraft:snowy_slopes";
						} else {
							return "minecraft:grove";
						}
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				} else {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else if (temperatureLevel == 3) {
						return "minecraft:stony_peaks";
					} else {
						if (weirdnessValue < 0.0) {
							return "minecraft:jagged_peaks";
						} else {
							return "minecraft:frozen_peaks";
						}
					}
				}
			}
		} else if (erosionLevel == 2) {
			if (pvLevel == PVLevel.Valleys) {
				if (temperatureLevel == 0) {
					return "minecraft:frozen_river";
				} else {
					return "minecraft:river";
				}
			} else if (pvLevel == PVLevel.Low) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					return "minecraft:stony_shore";
				} else if (continentalnessLevel == ContinentalnessLevel.Near_inland) {
					return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
				} else {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				}
			} else if (pvLevel == PVLevel.Mid) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					return "minecraft:stony_shore";
				} else if (continentalnessLevel == ContinentalnessLevel.Near_inland) {
					return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
				} else if (continentalnessLevel == ContinentalnessLevel.Mid_inland) {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				} else {
					return getPlateau(humidityLevel, temperatureLevel, weirdnessValue, false);
				}
			} else {
				if (continentalnessLevel == ContinentalnessLevel.Coast ||
					continentalnessLevel == ContinentalnessLevel.Near_inland) {
					return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
				} else {
					return getPlateau(humidityLevel, temperatureLevel, weirdnessValue, false);
				}
			}
		} else if (erosionLevel == 3) {
			if (pvLevel == PVLevel.Valleys) {
				if (temperatureLevel == 0) {
					return "minecraft:frozen_river";
				} else {
					return "minecraft:river";
				}
			} else if (pvLevel == PVLevel.Low) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					return getBeach(temperatureLevel);
				} else if (continentalnessLevel == ContinentalnessLevel.Near_inland) {
					return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
				} else {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				}
			} else if (pvLevel == PVLevel.Mid) {
				if (continentalnessLevel == ContinentalnessLevel.Coast ||
					continentalnessLevel == ContinentalnessLevel.Near_inland) {
					return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
				} else {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				}
			} else {
				if (continentalnessLevel == ContinentalnessLevel.Coast ||
					continentalnessLevel == ContinentalnessLevel.Near_inland) {
					return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
				} else if (continentalnessLevel == ContinentalnessLevel.Mid_inland) {
					if (temperatureLevel == 4) {
						return getBadland(humidityLevel, weirdnessValue, false);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				} else {
					return getPlateau(humidityLevel, temperatureLevel, weirdnessValue, false);
				}
			}
		} else if (erosionLevel == 4) {
			if (pvLevel == PVLevel.Valleys) {
				if (temperatureLevel == 0) {
					return "minecraft:frozen_river";
				} else {
					return "minecraft:river";
				}
			} else if (pvLevel == PVLevel.Low) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					return getBeach(temperatureLevel);
				} else {
					return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
				}
			} else if (pvLevel == PVLevel.Mid) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					if (weirdnessValue < 0.0) {
						return getBeach(temperatureLevel);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				} else {
					return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
				}
			} else {
				return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
			}
		} else if (erosionLevel == 5) {
			if (pvLevel == PVLevel.Valleys) {
				if (temperatureLevel == 0) {
					return "minecraft:frozen_river";
				} else {
					return "minecraft:river";
				}
			} else if (pvLevel == PVLevel.Low) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					if (weirdnessValue < 0.0) {
						return getBeach(temperatureLevel);
					} else if (temperatureLevel == 0 || temperatureLevel == 1 || humidityLevel == 4) {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					} else {
						return "minecraft:windswept_savanna";
					}
 				} else if (continentalnessLevel == ContinentalnessLevel.Near_inland) {
					if (weirdnessValue < 0.0 || temperatureLevel == 0 || temperatureLevel == 1 || humidityLevel == 4) {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					} else {
						return "minecraft:windswept_savanna";
					}
				} else {
					return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
				}
			}  else if (pvLevel == PVLevel.Mid) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					if (weirdnessValue < 0.0) {
						return getBeach(temperatureLevel);
					} else if (temperatureLevel == 0 || temperatureLevel == 1 || humidityLevel == 4) {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					} else {
						return "minecraft:windswept_savanna";
					}
 				} else if (continentalnessLevel == ContinentalnessLevel.Near_inland) {
					if (weirdnessValue < 0.0 || temperatureLevel == 0 || temperatureLevel == 1 || humidityLevel == 4) {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					} else {
						return "minecraft:windswept_savanna";
					}
				} else {
					return getShattered(humidityLevel, temperatureLevel, weirdnessValue, false);
				}
			} else if (pvLevel == PVLevel.High) {
				if (continentalnessLevel == ContinentalnessLevel.Coast ||
					continentalnessLevel == ContinentalnessLevel.Near_inland) {
					if (weirdnessValue < 0.0 || temperatureLevel == 0 || temperatureLevel == 1 || humidityLevel == 4) {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					} else {
						return "minecraft:windswept_savanna";
					}
				} else {
					return getShattered(humidityLevel, temperatureLevel, weirdnessValue, false);
				}
			} else {
				if (continentalnessLevel == ContinentalnessLevel.Coast ||
					continentalnessLevel == ContinentalnessLevel.Near_inland) {
					if (weirdnessValue < 0.0 || temperatureLevel == 0 || temperatureLevel == 1 || humidityLevel == 4) {
						return getShattered(humidityLevel, temperatureLevel, weirdnessValue, false);
					} else {
						return "minecraft:windswept_savanna";
					}
				} else {
					return getShattered(humidityLevel, temperatureLevel, weirdnessValue, false);
				}
			}
		} else {
			if (pvLevel == PVLevel.Valleys) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					if (temperatureLevel == 0) {
						return "minecraft:frozen_river";
					} else {
						return "minecraft:river";
					}
				} else {
					if (temperatureLevel == 0) {
						return "minecraft:frozen_river";
					} else if (temperatureLevel == 1 || temperatureLevel == 2) {
						return "minecraft:swamp";
					} else {
						return "minecraft:mangrove_swamp";
					}
				}
			} else if (pvLevel == PVLevel.Low) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					return getBeach(temperatureLevel);
				} else {
					if (temperatureLevel == 0) {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					} else if (temperatureLevel == 1 || temperatureLevel == 2) {
						return "minecraft:swamp";
					} else {
						return "minecraft:mangrove_swamp";
					}
				}
			} else if (pvLevel == PVLevel.Mid) {
				if (continentalnessLevel == ContinentalnessLevel.Coast) {
					if (weirdnessValue < 0.0) {
						return getBeach(temperatureLevel);
					} else {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					}
				} else {
					if (temperatureLevel == 0) {
						return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
					} else if (temperatureLevel == 1 || temperatureLevel == 2) {
						return "minecraft:swamp";
					} else {
						return "minecraft:mangrove_swamp";
					}
				}
			} else {
				return getMiddle(humidityLevel, temperatureLevel, weirdnessValue, false);
			}
		}

		return closest;
	}

	private double distanceToRange(double value, double min, double max) {
		if (value < min) return min - value;
		if (value > max) return value - max;
		return 0.0;
	}

	private String getBeach(int temperatureLevel) {
		if (temperatureLevel == 0) {
			return "minecraft:snowy_beach";
		}
		if (temperatureLevel == 4) {
			return "minecraft:desert";
		}
		return "minecraft:beach";
	}

	private String getBadland(int humidityLevel, double weirdnessValue, boolean flag) {
		if (humidityLevel == 0 || humidityLevel == 1) {
			if (0.0 < weirdnessValue || flag == true) {
				return "minecraft:eroded_badlands";
			} else {
				return "minecraft:badlands";
			}
		} else if (humidityLevel == 2 || humidityLevel == 3) {
			return "minecraft:badlands";
		}
		return "minecraft:wooded_badlands";
	}

	private String getMiddle(int humidityLevel, int temperatureLevel, double weirdnessValue, boolean flag) {
		if (humidityLevel == 0) {
			if (temperatureLevel == 0) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:ice_spikes";
				} else {
					return "minecraft:snowy_plains";
				}
			} else if (temperatureLevel == 1) {
				return "minecraft:plains";
			} else if (temperatureLevel == 2) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:sunflower_plains";
				} else {
					return "minecraft:flower_forest";
				}
			} else if (temperatureLevel == 3) {
				return "minecraft:savanna";
			} else {
				return "minecraft:desert";
			}
		} else if (humidityLevel == 1) {
			if (temperatureLevel == 0) {
				return "minecraft:snowy_plains";
			} else if (temperatureLevel == 1 || temperatureLevel == 2) {
				return "minecraft:plains";
			} else if (temperatureLevel == 3) {
				return "minecraft:savanna";
			} else {
				return "minecraft:desert";
			}
		} else if (humidityLevel == 2) {
			if (temperatureLevel == 0) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:snowy_taiga";
				} else {
					return "minecraft:snowy_plains";
				}
			} else if (temperatureLevel == 1 || temperatureLevel == 2) {
				return "minecraft:forest";
			} else if (temperatureLevel == 3) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:plains";
				} else {
					return "minecraft:forest";
				}
			} else {
				return "minecraft:desert";
			}
		} else if (humidityLevel == 3) {
			if (temperatureLevel == 0) {
				return "minecraft:snowy_taiga";
			} else if (temperatureLevel == 1) {
				return "minecraft:taiga";
			} else if (temperatureLevel == 2) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:old_growth_birch_forest";
				} else {
					return "minecraft:birch_forest";
				}
			} else if (temperatureLevel == 3) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:sparse_jungle";
				} else {
					return "minecraft:jungle";
				}
			} else {
				return "minecraft:desert";
			}
		} else {
			if (temperatureLevel == 0) {
				return "minecraft:taiga";
			} else if (temperatureLevel == 1) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:old_growth_pine_taiga";
				} else {
					return "minecraft:old_growth_spruce_taiga";
				}
			} else if (temperatureLevel == 2) {
				return "minecraft:dark_forest";
			} else if (temperatureLevel == 3) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:bamboo_jungle";
				} else {
					return "minecraft:jungle";
				}
			} else {
				return "minecraft:desert";
			}
		}
	}

	private String getPlateau(int humidityLevel, int temperatureLevel, double weirdnessValue, boolean flag) {
		if (humidityLevel == 0) {
			if (temperatureLevel == 0) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:ice_spikes";
				} else {
					return "minecraft:snowy_plains";
				}
			} else if (temperatureLevel == 1) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:cherry_grove";
				} else {
					return "minecraft:meadow";
				}
			} else if (temperatureLevel == 2) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:cherry_grove";
				} else {
					return "minecraft:meadow";
				}
			} else if (temperatureLevel == 3) {
				return "minecraft:savanna_plateau";
			} else {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:eroded_badlands";
				} else {
					return "minecraft:badlands";
				}
			}
		} else if (humidityLevel == 1) {
			if (temperatureLevel == 0) {
				return "minecraft:snowy_plains";
			} else if (temperatureLevel == 1) {
				return "minecraft:meadow";
			} else if (temperatureLevel == 2) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:cherry_grove";
				} else {
					return "minecraft:meadow";
				}
			} else if (temperatureLevel == 3) {
				return "minecraft:savanna_plateau";
			} else {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:eroded_badlands";
				} else {
					return "minecraft:badlands";
				}
			}
		} else if (humidityLevel == 2) {
			if (temperatureLevel == 0) {
				return "minecraft:snowy_plains";
			} else if (temperatureLevel == 1) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:meadow";
				} else {
					return "minecraft:forest";
				}
			} else if (temperatureLevel == 2) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:forest";
				} else {
					return "minecraft:meadow";
				}
			} else if (temperatureLevel == 3) {
				return "minecraft:forest";
			} else {
				return "minecraft:badlands";
			}
		} else if (humidityLevel == 3) {
			if (temperatureLevel == 0) {
				return "minecraft:snowy_taiga";
			} else if (temperatureLevel == 1) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:meadow";
				} else {
					return "minecraft:taiga";
				}
			} else if (temperatureLevel == 2) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:birch_forest";
				} else {
					return "minecraft:meadow";
				}
			} else if (temperatureLevel == 3) {
				return "minecraft:forest";
			} else {
				return "minecraft:wooded_badlands";
			}
		} else {
			if (temperatureLevel == 0) {
				return "minecraft:snowy_taiga";
			} else if (temperatureLevel == 1) {
				if (0.0 < weirdnessValue || flag == true) {
					return "minecraft:old_growth_pine_taiga";
				} else {
					return "minecraft:old_growth_spruce_taiga";
				}
			} else if (temperatureLevel == 2) {
				return "minecraft:pale_garden";
			} else if (temperatureLevel == 3) {
				return "minecraft:jungle";
			} else {
				return "minecraft:wooded_badlands";
			}
		}
	}

	private String getShattered(int humidityLevel, int temperatureLevel, double weirdnessValue, boolean flag) {
		if (temperatureLevel == 4) {
			return "minecraft:desert";
		} else if (humidityLevel == 0 || humidityLevel == 1) {
			if (temperatureLevel == 0 || temperatureLevel == 1) {
				return "minecraft:windswept_gravelly_hills";
			} else if (temperatureLevel == 2) {
				return "minecraft:windswept_hills";
			} else {
				return "minecraft:savanna";
			}
		} else if (humidityLevel == 2) {
			if (temperatureLevel == 0 || temperatureLevel == 1 || temperatureLevel == 2) {
				return "minecraft:windswept_hills";
			} else {
				if (flag == true || 0.0 < weirdnessValue) {
					return "minecraft:plains";
				} else {
					return "minecraft:forest";
				}
			}
		} else if (humidityLevel == 3) {
			if (temperatureLevel == 0 || temperatureLevel == 1 || temperatureLevel == 2) {
				return "minecraft:windswept_forest";
			} else {
				if (flag == true || 0.0 < weirdnessValue) {
					return "minecraft:sparse_jungle";
				} else {
					return "minecraft:jungle";
				}
			}
		} else {
			if (temperatureLevel == 0 || temperatureLevel == 1 || temperatureLevel == 2) {
				return "minecraft:windswept_forest";
			} else {
				if (flag == true || 0.0 < weirdnessValue) {
					return "minecraft:bamboo_jungle";
				} else {
					return "minecraft:jungle";
				}
			}
		}
	}
}
