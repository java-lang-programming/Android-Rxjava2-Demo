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

package java_lang_programming.com.android_rxjava2_demo.article78;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.DisposableSubscriber;
import java_lang_programming.com.android_rxjava2_demo.R;

import static java_lang_programming.com.android_rxjava2_demo.R.id.body_weight;


/**
 * Screen for BmiCalculationApplication
 */
public class BmiCalculationActivity extends AppCompatActivity {

    private static final String TAG = "BmiCalculationActivity";

    // エラー入力
    // Error Input
    public final static float ERROR_INPUT_VALUE = -1;

    private EditText heightView;
    private EditText bodyWeightView;
    private TextView bmiView;
    private TextView msgView;

    PublishProcessor<Float> height = PublishProcessor.create();
    PublishProcessor<Float> bodyWeight = PublishProcessor.create();
    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculation);

        heightView = (EditText) findViewById(R.id.height);
        heightView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                height.onNext(convertFloat(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bodyWeightView = (EditText) findViewById(body_weight);
        bodyWeightView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bodyWeight.onNext(convertFloat(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bmiView = (TextView) findViewById(R.id.bmi);
        msgView = (TextView) findViewById(R.id.msg);

        // 身長通知(Producer)
        Disposable disposableHeight = height.
                // 消費者のスレッド
                observeOn(AndroidSchedulers.mainThread()).
                // 購読(Consumer)
                subscribeWith(new DisposableSubscriber<Float>() {
                    @Override
                    public void onNext(Float height) {
                        updateBmiView(convertFloat(bodyWeightView.getText().toString()), height);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        // 体重通知(Producer)
        Disposable disposableWeight = bodyWeight.
                observeOn(AndroidSchedulers.mainThread()).
                // 購読(Consumer)
                subscribeWith(new DisposableSubscriber<Float>() {
                    @Override
                    public void onNext(Float bodyWeight) {
                        updateBmiView(bodyWeight, convertFloat(heightView.getText().toString()));
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        compositeDisposable = new CompositeDisposable(disposableHeight, disposableWeight);
    }

    /**
     * BMIを計算する
     * calculate BMI
     *
     * @param bodyWeight bodyWeight
     * @param height     height
     * @return bmi
     */
    private float calculateBmi(float bodyWeight, float height) {
        if (bodyWeight == ERROR_INPUT_VALUE || height == ERROR_INPUT_VALUE) {
            return ERROR_INPUT_VALUE;
        }
        return bodyWeight / (height * height);
    }

    /**
     * BMIView更新
     * update bmiTextView
     *
     * @param weight weight
     * @param height height
     */
    private void updateBmiView(float weight, float height) {
        float bmi = calculateBmi(weight, height);
        if (bmi == ERROR_INPUT_VALUE) {
            msgView.setText(getString(R.string.error));
            bmiView.setText(getString(R.string.error));
            return;
        }

        bmiView.setText(String.valueOf(bmi));

        String result = "wrong";
        @ColorRes int color = R.color.colorBlack;
        if (16 > bmi) {
            result = " probably sick ";
            color = R.color.colorRed;
        } else if (16 < bmi && bmi < 18.5) {
            result = " beautiful ";
        } else if (18.5 <= bmi && bmi < 22.0) {
            result = " standard ";
        } else if (22.0 <= bmi && bmi < 30.0) {
            result = " fat ";
        } else if (bmi > 30) {
            result = " sumo wrestler ";
            color = R.color.colorRed;
        }
        msgView.setTextColor(ContextCompat.getColor(getApplicationContext(), color));
        msgView.setText(getString(R.string.msg, result));
    }

    /**
     * 文字列をFloatに変換する
     * 変換に失敗した場合はERROR_INPUT_VALUEを返す
     *
     * @param str 任意の文字列
     * @return result float value
     */
    private float convertFloat(@NonNull String str) {
        try {
            return Float.valueOf(str);
        } catch (NumberFormatException e) {
            return ERROR_INPUT_VALUE;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Log.d(TAG, "onDestroy " + compositeDisposable.isDisposed());
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        // Log.d(TAG, "onDestroy " + compositeDisposable.isDisposed());
    }
}
