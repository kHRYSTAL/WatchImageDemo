package photoview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class BigPicViewPage extends ViewPager {

	public BigPicViewPage(Context context) {
		super(context);
	}

	public BigPicViewPage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (Exception e) {
			// IllegalArgument
			e.printStackTrace();
			return false;
		}
	}

}