package vladimir.yandex;

import android.content.Intent;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


import vladimir.yandex.activities.GalleryActivity;
import vladimir.yandex.activities.PhotoActivity;


@RunWith(AndroidJUnit4.class)
public class ClickInstrumentedTest {

    @Rule
    public ActivityTestRule<PhotoActivity> rule =
            new ActivityTestRule(PhotoActivity.class, true, false);


    //Здесь тестирую запуск активити и проверяю, совпадает ли тайтл тулбара с тем, что в нее отправили
    //Просто взять текст из тулбара нельзя, поэтому нужно писать свой Matcher
    //В мэтчере переписываю метод корректности проверки и соответствующее сообшение
    @Test
    public void ensureClickWorks(){
        Intent intent = new Intent();
        intent.putExtra(Constants.NAME, "Ants in my Eyes Johnson");
        rule.launchActivity(intent);
        onView(withId(R.id.photoToolbar)).check(matches(withToolbarTitle("Ants in my Eyes Johnson")));
    }
    public static Matcher<View> withToolbarTitle(CharSequence title) {
        return withToolbarTitle(is(title));
    }

    public static Matcher<View> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<View, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

}
