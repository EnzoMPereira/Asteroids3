package io.enzo.extras;

import processing.core.PVector;

public abstract class MyMath {
	public static float sign(float n) {
		if (n == 0)
			return 0;

		return Math.abs(n) / n;
	}

	public static boolean onSegment(PVector p, PVector q, PVector r) {
		return (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && q.y <= Math.max(p.y, r.y)
				&& q.y >= Math.min(p.y, r.y));
	}

	public static int orientation(PVector p, PVector q, PVector r) {
		int val = (int) Math.round(((q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y)));

		if (val == 0)
			return 0;
		return (val > 0) ? 1 : 2;
	}

	public static boolean doIntersect(PVector p1, PVector q1, PVector p2, PVector q2) {

		int o1 = orientation(p1, q1, p2);
		int o2 = orientation(p1, q1, q2);
		int o3 = orientation(p2, q2, p1);
		int o4 = orientation(p2, q2, q1);

		if (o1 != o2 && o3 != o4)
			return true;

		if (o1 == 0 && onSegment(p1, p2, q1))
			return true;

		if (o2 == 0 && onSegment(p1, q2, q1))
			return true;

		if (o3 == 0 && onSegment(p2, p1, q2))
			return true;

		if (o4 == 0 && onSegment(p2, q1, q2))
			return true;

		return false;
	}

}
