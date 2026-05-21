package worldgen.density_function.other_density_functions;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import utils.math.noise.ANoise;
import worldgen.density_function.Density_function;
import utils.math.noise.INoise;

public final class Spline implements INoise {
	private Data data;
	private INoise coordinate;
	private Point[] points;

	public Spline(Data data, INoise coordinate, JSONArray points) throws Exception {
		this.data = data;
		this.coordinate = coordinate;
		this.points = new Point[points.length()];
		for (int i = 0; i < points.length(); i++) {
			this.points[i] = new Point(this.data, points.getJSONObject(i).getFloat("location"), points.getJSONObject(i).get("value"), points.getJSONObject(i).getFloat("derivative"));
		}
	}

	@Override
	public String getNoise_type() {
		return "Spline";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		double coordinate = this.coordinate.sample3D(x, y, z);
		int location = binarySearch_findLocation(coordinate, 0, this.points.length) - 1;
		int length = this.points.length - 1;

		if (location < 0) {
			return this.points[0].getValue(x, y, z) + this.points[0].derivative * (coordinate - this.points[0].location);
		}
		if (location == length) {
			return this.points[length].getValue(x, y, z) + this.points[length].derivative * (coordinate - this.points[length].location);
		}
		float loc0 = this.points[location].location;
		float loc1 = this.points[location + 1].location;
		float der0 = this.points[location].derivative;
		float der1 = this.points[location + 1].derivative;
		if (loc1 - loc0 == 0) {
			return this.points[location].getValue(x, y, z);
		}
		float f = ((float)coordinate - loc0) / (loc1 - loc0);

		float val0 = this.points[location].getValue(x, y, z);
		float val1 = this.points[location + 1].getValue(x, y, z);

		float f8 = (der0 * (loc1 - loc0)) - (val1 - val0);
		float f9 = (-der1 * (loc1 - loc0)) + (val1 - val0);
		double f10 = ANoise.lerp(val0, val1, f) + (f * (1.0 - f)) * ANoise.lerp(f8, f9, f);

		return f10;
	}

	protected float compute(double x, double y, double z) {
		return (float)sample3D(x, y, z);
	}

	private int binarySearch_findLocation(double coordinate, int low, int high) {
		while (low < high) {
			int mid = (low + high) / 2;
			if (coordinate < this.points[mid].location) {
				high = mid;
			} else {
				low = mid + 1;
			}
		}
		return low;
	}

	protected class Point {
		private Data data;
		protected final float location;
		private Float value = null;
		private Spline spline_value = null;
		protected final float derivative;

		protected Point(Data data, float location, Object value, float derivative) throws Exception {
			this.data = data;
			this.location = location;
			if (value instanceof Number) {
				this.value = ((Number)value).floatValue();
			} else{
				JSONObject valueObject = (JSONObject)value;
				INoise spline_arg_coordinate = Density_function.parse(data, valueObject.get("coordinate"));
				this.spline_value = new Spline(this.data, spline_arg_coordinate, valueObject.getJSONArray("points"));
			}
			this.derivative = derivative;
		}

		protected float getValue(double x, double y, double z) {
			if (this.value != null) {
				return this.value;
			} else if (spline_value != null){
				return this.spline_value.compute(x, y, z);
			} else {
				throw new RuntimeException("worldgen.density_function.other_density_functions.Spline.Point | Both value and spline_value are null.");
			}
		}
	}
}
