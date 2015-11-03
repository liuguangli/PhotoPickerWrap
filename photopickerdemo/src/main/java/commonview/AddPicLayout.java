package commonview;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

import me.iwf.PhotoPickerDemo.R;


public class AddPicLayout extends ViewGroup {
    private int rowSize = 3;
    private int childPadding = 0;
    private int maxSize = 9;
    private LayoutParams childParam;
    private OnPreviewListener listener;
    public AddPicLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.addPic);
        rowSize = a.getInt(R.styleable.addPic_row_size,3);
        maxSize = a.getInt(R.styleable.addPic_max_size,8);
        childPadding = (int)a.getDimension(R.styleable.addPic_child_padding, 0);

    }

    public View getChildById(int id){
        int  count = getChildCount();
        for (int i = 0;i<count;i++){
            View child = getChildAt(i);
            if (id==child.getId()){
                return child;
            }
        }
        return null;
    }

    public void setPaths(ArrayList<String> paths){
        clear();
        for (int i = 0;i<paths.size();i++){
            ImageView imageView = new ImageView(getContext());
            addView(imageView);
            imageView.setId(i);
            Uri uri = Uri.fromFile(new File(paths.get(i)));
            Glide.with(getContext())
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .placeholder(me.iwf.photopicker.R.drawable.ic_photo_black_48dp)
                    .error(me.iwf.photopicker.R.drawable.ic_broken_image_black_48dp)
                    .into(imageView);

        }

        if (paths.size() < maxSize){
           addPlusPic(maxSize);
        }

    }

    private void addPlusPic(int index) {
        ImageView addImage = new ImageView(getContext());
        addImage.setId(index);
        addImage.setImageResource(R.mipmap.bg_add_pic);
        addView(addImage);
        addImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onPick();
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addPlusPic(1);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        child.setLayoutParams(getChildParam());
        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onPreview(view.getId(), true);
                }
            }
        });
    }
    public void clear(){
        this.removeAllViews();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new LinearLayout.LayoutParams(getContext(),attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        Log.d(getClass().getName(), "onMeasure");
        int  count = getChildCount();
        if (count>maxSize){
            count = maxSize;
        }
        if (count>0){
            View child = getChildAt(0);
            LayoutParams params = child.getLayoutParams();
            int  cHeight = params.height;
            int lineNum = count/rowSize;
            lineNum = count%rowSize==0?lineNum:lineNum+1;
            int maxH =(2*childPadding+cHeight)*lineNum;
            setMeasuredDimension(resolveSize(getMeasuredWidth(), widthMeasureSpec),
                    resolveSize(maxH, heightMeasureSpec));
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(getClass().getName(), "onLayout");
        final int childCount =  getChildCount();

        for (int i = 0;i<childCount;i++){
            final View child = getChildAt(i);
            if (i>=maxSize){
                child.setVisibility(View.GONE);
            } else{
                child.setVisibility(View.VISIBLE);
                LayoutParams params = child.getLayoutParams();
                int  cWidth = params.width;
                int  cHeight = params.height;
                int cl = childPadding+ (i%rowSize)*(cWidth+childPadding);
                int cr = cl+cWidth;
                int ct = childPadding+ (i/rowSize)*(cHeight+childPadding);
                int cb = ct+cHeight;
                child.layout(cl,ct,cr,cb);
            }
    }

    }


    public LayoutParams getChildParam() {
        if (childParam == null){
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int childWidth = (dm.widthPixels-(rowSize+1)*childPadding)/rowSize;
            childParam = new LayoutParams(childWidth,childWidth);
        }
        return childParam;
    }

    public void setOnPreviewListener(OnPreviewListener listener){
        this.listener = listener;
    }



}
