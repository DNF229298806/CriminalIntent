package example.com.criminalintent;

/**
 * @author Richard_Y_Wang
 * @version $Rev$
 * @des 2018/5/22
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public interface ItemTouchHelperAdapter {
    //数据交换
    void onItemMove(int fromPosition, int toPosition);

    //数据删除
    void onItemDismiss(int position);
}
