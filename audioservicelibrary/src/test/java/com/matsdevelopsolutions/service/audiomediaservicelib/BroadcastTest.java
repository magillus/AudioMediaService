package com.matsdevelopsolutions.service.audiomediaservicelib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.IntRange;
import android.test.mock.MockContext;

import com.matsdevelopsolutions.service.audiomediaservicelib.receiver.MediaBufferProgressBroadcastReceiver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by mateusz on 9/14/15.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=18)
public class BroadcastTest {
    Context context;

    @Before
    public void startup() {

        context = Mockito.mock(MyContext.class);

    }

    public final class MyContext extends MockContext {

        public MyContext(String test) {
            super();
        }

        Map<IntentFilter, BroadcastReceiver> receiverMap = new HashMap<>();
        @Override
        public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
            receiverMap.put(filter, receiver);
            return null;
        }

        @Override
        public void sendBroadcast(Intent intent) {
            for (IntentFilter filter : receiverMap.keySet()) {
                if (filter.matchAction(intent.getAction())) {
                    receiverMap.get(filter).onReceive(this, intent);
                }
            }
        }
    }


    @Test
    public void testForBroadcast() {

        MediaBufferProgressBroadcastReceiver receiver =new MediaBufferProgressBroadcastReceiver() {
            @Override
            public void onBufferProgressChanged(@IntRange(from = 0, to = 100) int progress) {
                assertEquals(progress, 40);
            }
        };
        IntentFilter intentFilter = MediaBufferProgressBroadcastReceiver.getIntentFilter();

        Mockito.mock(Context.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return new MyContext("test");
            }
        });
        MediaBufferProgressBroadcastReceiver.register(context, receiver);
        IntentBroadcaster broadcaster = new IntentBroadcaster(context);
        broadcaster.buffering(40);

    }
}
