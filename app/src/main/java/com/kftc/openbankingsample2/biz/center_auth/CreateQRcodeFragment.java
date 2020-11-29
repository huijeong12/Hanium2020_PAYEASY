package com.kftc.openbankingsample2.biz.center_auth;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.main.HomeFragment;

import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class CreateQRcodeFragment extends AbstractCenterAuthMainFragment {
    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;

    private static String total_price ="0";

    // timer
    private static final long START_TIME_IN_MILLIS = 30000;
    private TextView mTextViewCountDown;
    private Button mButtonComplete;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    // QR code generator
    EditText qrvalue;
    Button generateBtn,scanBtn;
    ImageView qrImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 메뉴 창에서 가격 받아오기
        total_price = getArguments().getString("total_price_arg");
        view = inflater.inflate(R.layout.fragment_create_qrcode, container, false);
        initView();
        return view;
    }

    void initView() {

        mTextViewCountDown = (TextView) view.findViewById(R.id.TimerCountDownText); // QR 코드가 유지되는 시간을 나타내는 TextView
        mButtonComplete = view.findViewById(R.id.btnCompleteQRCreation); // 완료 버튼
        mButtonReset = view.findViewById(R.id.btnExtendQRTime); // 시간 연장 버튼

        // 완료 버튼을 누르면
        mButtonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 시간을 멈추고 알림을 띄운다
                pauseTimer();
                showAlertDialog();
            }
        });

        // 시간 연장 버튼을 누르면
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 시간을 30초로 재설정한다
                resetTimer();
            }
        });

        // 이 화면에 들어오면 시간을 처음부터 줄어들어야 한다
        startTimer();
        updateCountDownText();

        // QR 코드 이미지
        qrImage = view.findViewById(R.id.QRimageView);

        // QR 만들기
        makeQR();

    }

    // Timer를 시작한다
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                showAlertDialog();
            }
        }.start();

        mTimerRunning = true;
    }

    // Timer를 멈춘다
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }

    // Timer를 초기화 한다
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        pauseTimer();
        startTimer();
    }

    // Timer를 1초에 1씩 줄어든게 한다
    private void updateCountDownText() {

        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    // 완료 버튼을 누르면 이 함수 실행 - alertDialog를 띄운다
    void showAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("QR코드 결제 완료");
        builder.setMessage("확인 버튼을 누르면 메인화면으로 이동합니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"판매자 메인화면으로 이동합니다.",Toast.LENGTH_LONG).show();
                        startFragment(CenterAuthHomeFragment.class, args, R.string.fragment_id_home);
                    }
                });

        builder.show();
    }

    // QR 코드를 생성한다
    public void makeQR() {
        // 이전의 메뉴 선택 화면에서의 total_price를 가져와 QR 코드를 만든다
        String data = total_price + " Pay:easy-입금 한이음분식 김오픈 097 232000067812";
        /*
         *금액 1000
         *입금계좌인자내역 어플이름-입금
         *최종수취고객성명 김오픈
         *최종수취고객계좌표준코드 197
         *최종수취고객계좌번호 232000067812
         * */

        if(data.isEmpty()){
            Toast.makeText(context, "value required",Toast.LENGTH_LONG);
        }else {

            QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 500);
            Bitmap qrBits = qrgEncoder.getBitmap();
            qrImage.setImageBitmap(qrBits);

        }
    }
}