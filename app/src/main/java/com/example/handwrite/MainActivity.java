package com.example.handwrite;

import android.Manifest;
import android.os.ParcelFileDescriptor;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import java.io.InputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_FONT_FILE_REQUEST_CODE = 2;
    private static final int PICK_BACKGROUND_IMAGE_REQUEST_CODE = 3;

    private EditText inputText;
    private ImageView imagePreview;
    private Button selectBackgroundButton, selectFontFileButton, selectColorButton, exportButton;
    private SeekBar fontSizeSeekBar;
    private Spinner fontSpinner;
    private SeekBar lineSpacingSeekBar;
    private SeekBar textAreaWidthSeekBar; // 新增：文字显示区域宽度调节 SeekBar
    private SeekBar textAreaHeightSeekBar; // 新增：文字显示区域高度调节 SeekBar
    private Bitmap backgroundBitmap;
    private Typeface selectedFont;
    private int selectedColor = Color.BLACK;
    private int fontSize = 20;
    private int lineSpacing = 10;
    private int textAreaWidth = 300; // 新增：文字显示区域默认宽度
    private int textAreaHeight = 200; // 新增：文字显示区域默认高度
    private List<String> fontNames = new ArrayList<>();
    private List<Typeface> fonts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.input_text);
        imagePreview = findViewById(R.id.image_preview);
        selectBackgroundButton = findViewById(R.id.select_background_button);
        selectFontFileButton = findViewById(R.id.select_font_file_button);
        selectColorButton = findViewById(R.id.select_color_button);
        exportButton = findViewById(R.id.export_button);
        fontSizeSeekBar = findViewById(R.id.font_size_seekbar);
        fontSpinner = findViewById(R.id.font_spinner);
        lineSpacingSeekBar = findViewById(R.id.line_spacing_seekbar);
        textAreaWidthSeekBar = findViewById(R.id.text_area_width_seekbar);
        textAreaHeightSeekBar = findViewById(R.id.text_area_height_seekbar);

        // 权限检查和请求
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        }

        loadFonts();
        setupFontSpinner();

        selectBackgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_BACKGROUND_IMAGE_REQUEST_CODE);
            }
        });

        selectFontFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("font/*");
                startActivityForResult(intent, PICK_FONT_FILE_REQUEST_CODE);
            }
        });

        selectColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog();
            }
        });

        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fontSize = progress;
                updatePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        lineSpacingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lineSpacing = progress;
                updatePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        textAreaWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textAreaWidth = progress;
                updatePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        textAreaHeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textAreaHeight = progress;
                updatePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportImage();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadFonts() {
        try {
            String[] fontFiles = getAssets().list("fonts");
            for (String fontFile : fontFiles) {
                fontNames.add(fontFile.replace(".ttf", "").replace(".otf", ""));
                fonts.add(Typeface.createFromAsset(getAssets(), "fonts/" + fontFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupFontSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fontNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSpinner.setAdapter(adapter);

        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFont = fonts.get(position);
                updatePreview();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_BACKGROUND_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                backgroundBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                updatePreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_FONT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri fontUri = data.getData();
                if (fontUri != null) {
                    loadFontFromUri(fontUri);
                }
            }
        }
    }

    private void loadFontFromUri(Uri fontUri) {
        try {
            // 获取文件描述符
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(fontUri, "r");
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                // 从文件描述符创建字体
                File fontFile = new File(fileDescriptor.toString());
                selectedFont = Typeface.createFromFile(fontFile);
                parcelFileDescriptor.close(); // 关闭文件描述符
                updatePreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "加载字体失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePreview() {
        if (backgroundBitmap == null) {
            backgroundBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
            backgroundBitmap.eraseColor(Color.WHITE);
        }
        Bitmap bitmap = backgroundBitmap.copy(backgroundBitmap.getConfig(), true);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(selectedColor);
        paint.setTextSize(fontSize);
        if (selectedFont != null) {
            paint.setTypeface(selectedFont);
        }

        String text = inputText.getText().toString();
        Rect bounds = new Rect();
        paint.getTextBounds("A", 0, 1, bounds);
        int lineHeight = bounds.height() + lineSpacing;

        int x = 100;
        int y = 100;
        // 确保使用正确的宽度值
        int maxWidth = x + textAreaWidth;
        int maxHeight = y + textAreaHeight;

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            bounds = new Rect();
            paint.getTextBounds(testLine.toString(), 0, testLine.length(), bounds);
            if (x + bounds.width() > maxWidth) {
                if (y + lineHeight > maxHeight) {
                    break;
                }
                canvas.drawText(currentLine.toString(), x, y, paint);
                y += lineHeight;
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        if (currentLine.length() > 0 && y + lineHeight <= maxHeight) {
            canvas.drawText(currentLine.toString(), x, y, paint);
        }

        imagePreview.setImageBitmap(bitmap);
    }

    private void exportImage() {
        Bitmap bitmap = getBitmapFromView(imagePreview);
        File file = saveBitmapToFile(bitmap);
        if (file != null) {
            Toast.makeText(this, "图片已保存: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromView(ImageView view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "hand_font_image.png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showColorPickerDialog() {
        new ColorPickerDialog.Builder(this)
                .setTitle("颜色选择器")
                .setPositiveButton("确定", new ColorEnvelopeListener() {
                    @Override
                    public void onColorSelected(com.skydoves.colorpickerview.ColorEnvelope envelope, boolean fromUser) {
                        selectedColor = envelope.getColor();
                        updatePreview();
                    }
                })
                .setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show();
    }
}