package com.example.irina.mp3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Irina on 16.12.2016.
 */
public class BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Product> objects;

    BoxAdapter (Context context, ArrayList<Product> products){
        ctx = context;
        objects = products;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override //кол-во элементов
    public int getCount() {
        return objects.size();
    }

    @Override //эл-т по позиции
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override //id по позиции
    public long getItemId(int position) {
        return position;
    }

    @Override //пункт списка
    public View getView(int position, View convertView, ViewGroup parent) {
        //используем созданные, но не используемые view
        View view = convertView;
        if (view == null){
            view = lInflater.inflate(R.layout.item, parent, false);
        }
        Product p = getProduct(position);
        //заполняем View в пункте списка данными из товаров: наименование, цена, картинка
        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.name);
        ((TextView) view.findViewById(R.id.tvPrice)).setText(p.price + "");
        ((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);
        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
        //пррисваиваем чекбоксу обработчик
        cbBuy.setOnCheckedChangeListener(myCheckChangeList);
        //пишем позицию
        cbBuy.setTag(position);
        //заполняем данными из товаров: в корзине или нет
        cbBuy.setChecked(p.box);
        return view;
    }

    //товар по позиции
    Product getProduct (int position){
        return ((Product) getItem(position));
    }

    //содержимое корзины
    ArrayList<Product> getBox (){
        ArrayList<Product> box = new ArrayList<Product>();
        for (Product p : objects){
            //если в корзине
            if (p.box)
                box.add(p);
        }
        return box;
    }

    //обработчик для чекбоксов
    CompoundButton.OnCheckedChangeListener myCheckChangeList = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //меняем данные товара (в корзмне или нет)
            getProduct((Integer) buttonView.getTag()).box = isChecked;
        }
    };
}
