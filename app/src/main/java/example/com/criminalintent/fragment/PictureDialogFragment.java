package example.com.criminalintent.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import example.com.criminalintent.PictureUtils;
import example.com.criminalintent.R;

/**
 * @author Richard_Y_Wang
 * @version $Rev$
 * @des 2018/5/22
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class PictureDialogFragment extends DialogFragment {
    public static PictureDialogFragment newInstance(String path) {
        Bundle args = new Bundle();
        args.putString("path", path);
        PictureDialogFragment fragment = new PictureDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String path = getArguments().getString("path");
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_picture, null);
        ImageView imageView = view.findViewById(R.id.iv_see_picture);
        imageView.setImageBitmap(PictureUtils.getScaledBitmap(path, getActivity()));
        final AlertDialog a = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("看大图")
                .setPositiveButton(android.R.string.ok, null)
                .create();
        //设置点击事件，当点击图片时候，dialog消失。
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.dismiss();
            }
        });
        return a;
        /*//获取到之前传过来的路径
        String path = getArguments().getString("path");
        //创建一个dialog
        final Dialog dialog = new Dialog(getActivity());
        //设置dialog的布局,为之前创建的布局文件,里面仅有一个ImageView
        dialog.setContentView(R.layout.dialog_picture);
        //找到控件
        ImageView imageView = dialog.findViewById(R.id.iv_see_picture);
        //使用 PictureUtils 类的工具来获得缩放的 Bitmap
        imageView.setImageBitmap(PictureUtils.getScaledBitmap(path, getActivity()));
        //设置点击事件，当点击图片时候，dialog消失。
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;*/

    }
}
