/**
 * Copyright (C) 2017 Programming Java Android Development Project
 * Programming Java is
 * <p>
 * http://java-lang-programming.com/
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java_lang_programming.com.android_rxjava2_demo.article77;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDateTime;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import java_lang_programming.com.android_rxjava2_demo.R;

/**
 * Screen for TimeDisplayApplication
 */
public class TimeDisplayActivity extends AppCompatActivity {
    private static final String TAG = "TimeDisplayActivity";
    private TextView time;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_display);
        AndroidThreeTen.init(this);

        time = (TextView) findViewById(R.id.timer);

        // 生産者を作成(create Producer)
        disposable = Flowable.interval(1000L, TimeUnit.MILLISECONDS)
                // スレッド(thread)
                .observeOn(AndroidSchedulers.mainThread())
                // 購読(subscribe)
//              Rxjava2
//                .subscribeWith(new ResourceSubscriber<Long>(){
//                    @Override
//                    public void onNext(Long aLong) {
//                        updateTimeView();
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
                .subscribe(aLong -> {
                    updateTimeView();
                });
    }

    /**
     * update TimeTextView
     * Timeのviewを更新する
     */
    private void updateTimeView() {
        LocalDateTime current = LocalDateTime.now();
        StringBuffer str = new StringBuffer();
        str.append(current.getHour() + " : " + current.getMinute() + " : " + current.getSecond());
        time.setText(str.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
