package commonview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import me.iwf.PhotoPickerDemo.R;


public class ShowPicLayout extends ViewGroup {
    private static final int ROW_SIZE4 = 2;
    private int rowSize = 3;
    private int childPadding = 0;
    private int maxSize = 9;
    private LayoutParams childParam;
    private OnPreviewListener listener;
    public ShowPicLayout(Context context, AttributeSet attrs) {
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

    public void setPaths(ArrayList<String> urls){
        clear();
        childParam = null;
        int count = urls.size();
        if (count>1){
            for (int i = 0;i<urls.size();i++){
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(getChildParam());
                addView(imageView);
                imageView.setId(i);
                Glide.with(getContext())
                        .load(urls.get(i))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .placeholder(me.iwf.photopicker.R.drawable.ic_photo_black_48dp)
                        .error(me.iwf.photopicker.R.drawable.ic_broken_image_black_48dp)
                        .into(imageView);

            }
        } else {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(getOneChildParam(urls.get(0)));
            addView(imageView);
            imageView.setId(0);
            Glide.with(getContext())
                    .load(urls.get(0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(me.iwf.photopicker.R.drawable.ic_photo_black_48dp)
                    .thumbnail(0.1f)
                    .into(imageView);
        }

    }

    @Override
    public void addView(View child) {
        super.addView(child);
        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onPreview(view.getId(), false);
                }
            }
        });
    }

    private LayoutParams getOneChildParam(String url) {
        //todo 和后端商定，url里面携带尺寸信息,解析尺寸信息并计算缩放后的尺寸
        if (childParam == null){
            childParam = new LayoutParams(380,380);
        }
        return childParam;

    };


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
            int row = getRowSize(count);
            View child = getChildAt(0);
            LayoutParams params = child.getLayoutParams();
            int  cHeight = params.height;
            int lineNum = count/row;
            lineNum = count%row==0?lineNum:lineNum+1;
            int maxH =(2*childPadding+cHeight)*lineNum;
            setMeasuredDimension(resolveSize(getMeasuredWidth(), widthMeasureSpec),
                    resolveSize(maxH, heightMeasureSpec));
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(getClass().getName(), "onLayout");
        final int childCount =  getChildCount();

        int row = getRowSize(childCount);
        for (int i = 0;i<childCount;i++){
            final View child = getChildAt(i);
            if (i>=maxSize){
                child.setVisibility(View.GONE);
            } else{
                child.setVisibility(View.VISIBLE);
                LayoutParams params = child.getLayoutParams();
                int  cWidth = params.width;
                int  cHeight = params.height;
                int cl = childPadding+ (i%row)*(cWidth+childPadding);
                int cr = cl+cWidth;
                int ct = childPadding+ (i/row)*(cHeight+childPadding);
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


    public int getRowSize(int count) {
        int row = rowSize;
        if (count == 4){
            row = ROW_SIZE4;
        }
        return row;
    }
}
