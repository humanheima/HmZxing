package com.hm.hmzxing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hm.hmzxinglibrary.encode.HmEncoder;
import com.hm.hmzxinglibrary.util.ScreenUtil;

/**
 * 生成条形码，二维码
 */
public class EncodeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private EditText editContent;
    private Button btnOneD;
    private Button btnQrCode;
    private Button btnImageQrCode;
    //生成的条形码或者二维码的宽度
    private int bitmapWidth;
    //生成二维码的内容
    private String content;
    //生成二维码的图片地址
    private String bitmapUrl;
    //生成二维码的图片的资源id
    private String bitmapResId;

    public static void launch(Context context) {
        Intent starter = new Intent(context, EncodeActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);
        imageView = (ImageView) findViewById(R.id.image_view);
        editContent = (EditText) findViewById(R.id.edit_content);
        btnOneD = (Button) findViewById(R.id.btn_one_d);
        btnOneD.setOnClickListener(this);
        btnQrCode = (Button) findViewById(R.id.btn_qr_code);
        btnQrCode.setOnClickListener(this);
        btnImageQrCode = (Button) findViewById(R.id.btn_image_qr_code);
        btnImageQrCode.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_one_d) {
            generateOneD();
        } else if (v.getId() == R.id.btn_qr_code) {
            generateQrCode();
        } else if (v.getId() == R.id.btn_image_qr_code) {
            generateImageQrCode();
        }
    }

    /**
     * 生成条形码
     */
    private void generateOneD() {
        //这个9780201379624是EAN_13码
        bitmapWidth = ScreenUtil.dp2px(this, 140);
        Bitmap imageBitmap = HmEncoder.createBarCode(this, "9780201379624", bitmapWidth);
        imageView.setImageBitmap(imageBitmap);
    }

    /**
     * 生成二维码
     */
    private void generateQrCode() {
        bitmapWidth = ScreenUtil.getScreenWidth(this) / 9 * 7;
        content = editContent.getText().toString();
        Bitmap bitmap = HmEncoder.createQRCode(bitmapWidth, content);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 生成带图片的二维码
     */
    private void generateImageQrCode() {

        bitmapWidth = ScreenUtil.getScreenWidth(this) / 9 * 7;
        content = editContent.getText().toString();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        Bitmap result = HmEncoder.createQrCodeWithLogo(bitmapWidth, content, bitmap);
        imageView.setImageBitmap(result);
    }
}
