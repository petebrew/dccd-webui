package nl.knaw.dans.dccd.common.wicket.timeline;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TimeMarkerTest {

	   @Test
	   public void contructFromYearsTest()
	   {
		   final String INFO_STR = "Test this";
		   final int YEAR_MINUS_ONE = -1;
		   final int YEAR_ONE = 1;
		   
		   TimeMarker m = new TimeMarker(YEAR_MINUS_ONE, YEAR_ONE, INFO_STR);
		   DateTime from = m.getFrom();
		   DateTime to = m.getTo();
		   
		   // are DateTime's set correctly
		   assertEquals(INFO_STR, m.getInfo());
		   assertEquals(YEAR_MINUS_ONE, from.getYear());
		   assertEquals(YEAR_ONE, to.getYear());
		   
		   // time should span three years 
		   // from start of year -1 to whole of year 0 and the end of year 1 
		   assertEquals(Days.daysBetween(from, to).getDays(), 3*365); 
		   
		   // could test what happens if 'to' is before 'from',
		   // but the marker does not do anything with that 
	   }
}
