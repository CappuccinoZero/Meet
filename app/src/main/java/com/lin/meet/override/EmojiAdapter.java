package com.lin.meet.override;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lin.meet.R;

import java.util.ArrayList;
import java.util.List;

public class EmojiAdapter extends BaseAdapter {
    public static int[] emojis = {
            0x1F601,
            0x1F602,
            0x1F603,
            0x1F604,
            0x1F605,
            0x1F606,
            0x1F609,
            0x1F60A,
            0x1F60B,
            0x1F60C,
            0x1F60D,
            0x1F60E,
            0x1F60F,
            0x1F612,
            0x1F613,
            0x1F614,
            0x1F616,
            0x1F618,
            0x1F61A,
            0x1F61C,
            0x1F61D,
            0x1F61E,
            0x1F620,
            0x1F621,
            0x1F622,
            0x1F623,
            0x1F624,
            0x1F625,
            0x1F628,
            0x1F629,
            0x1F62A,
            0x1F62B,
            0x1F62D,
            0x1F630,
            0x1F631,
            0x1F632,
            0x1F633,
            0x1F634,
            0x1F635,
            0x1F637,
            0x1F638,
            0x1F639,
            0x1F63A,
            0x1F63B,
            0x1F63C,
            0x1F63D,
            0x1F63E,
            0x1F63F,
    };

    private List<String> emojiList = new ArrayList<>();

    @Override
    public int getCount() {
        return emojiList.size();
    }

    @Override
    public Object getItem(int position) {
        return emojiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmojiViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.emoji_view,parent,false);
            viewHolder = new EmojiViewHolder(convertView);
            viewHolder.emoji.setOnClickListener(v->{
                callback.getEmoji(emojiList.get(position));
            });
            convertView.setTag(viewHolder);
        }else
            viewHolder = (EmojiViewHolder)convertView.getTag();

        viewHolder.setEmoji(emojiList.get(position));
        return convertView;
    }

    public interface EmojiCallback{
        void getEmoji(String emoji);
    }

    private EmojiCallback callback;

    public EmojiAdapter(EmojiCallback callback){
        this.callback = callback;
        for(int emoji:emojis){
            emojiList.add(getEmojiStringByUnicode(emoji));
        }
    }

    class EmojiViewHolder{
        private TextView emoji;
        public EmojiViewHolder(View itemView) {
            emoji = (TextView)itemView.findViewById(R.id.emoji);
        }

        public void setEmoji(String emoji){
            this.emoji.setText(emoji);
        }
    }

    private static String getEmojiStringByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}
