package com.yjq.eyepetizer.ui.video.adapter

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.google.gson.Gson
import com.yjq.eyepetizer.CommonViewHolder
import com.yjq.eyepetizer.R
import com.yjq.eyepetizer.bean.cards.Item
import com.yjq.eyepetizer.bean.cards.item.VideoSmallCard
import com.yjq.eyepetizer.databinding.ItemBeanForClientCardBinding
import com.yjq.eyepetizer.databinding.ItemTheEndBinding
import com.yjq.eyepetizer.databinding.ItemVideoSmallCardBinding
import com.yjq.eyepetizer.inflate
import com.yjq.eyepetizer.ui.video.VideoPlayActivity
import com.yjq.eyepetizer.ui.video.bean.VideoBeanForClient
import com.yjq.eyepetizer.util.image.ImageLoader
import com.yjq.eyepetizer.util.time.TimeUtil
import retrofit2.http.POST

/**
 * 文件： VideoPlayAdapter
 * 描述：
 * 作者： YangJunQuan   2018-9-17.
 */
class VideoPlayAdapter(private val mContext: Context) : RecyclerView.Adapter<CommonViewHolder>() {


    //data
    private var mHeaderData: VideoBeanForClient? = null
    private var mRelatedDataList = ArrayList<Item>()


    enum class ItemType(var value: Int) {
        TYPE_HEADER(0),
        TYPE_RELATED(1),
        TYPE_THE_END(2);
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        val viewHolder =
                when (viewType) {
                    ItemType.TYPE_HEADER.value -> parent.inflate<ItemBeanForClientCardBinding>(R.layout.item_bean_for_client_card)
                    ItemType.TYPE_RELATED.value -> parent.inflate<ItemVideoSmallCardBinding>(R.layout.item_video_small_card)
                    ItemType.TYPE_THE_END.value -> parent.inflate<ItemTheEndBinding>(R.layout.item_the_end)
                    else -> throw Exception("invalid view type")
                }
        return CommonViewHolder(viewHolder.itemView)
    }


    override fun getItemCount(): Int {
        return 1 + mRelatedDataList.size + 1
    }


    override fun getItemViewType(position: Int): Int {

        return when (position) {
            0 -> ItemType.TYPE_HEADER.value
            in 1..mRelatedDataList.size -> ItemType.TYPE_RELATED.value
            else -> ItemType.TYPE_THE_END.value
        }
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {

        when (getItemViewType(position)) {
            ItemType.TYPE_HEADER.value -> initHeaderView(holder, position)
            ItemType.TYPE_RELATED.value -> initRelatedView(holder, position)
            ItemType.TYPE_THE_END.value -> initTheEndView(holder, position)
        }
    }


    /**
     * *********************************************** 以下是初始化各种不同类型ItemView 的渲染方法 ************************************************
     */

    private fun initHeaderView(holder: CommonViewHolder, position: Int) {

    }

    private fun initRelatedView(holder: CommonViewHolder, position: Int) {
        val itemVideoSmallCardBinding = DataBindingUtil.getBinding<ItemVideoSmallCardBinding>(holder.itemView)


        //init data
        val jsonObject = mRelatedDataList[position - 1].data
        val videoSmallCard = Gson().fromJson(jsonObject, VideoSmallCard::class.java)
        val videoJson = Gson().toJson(jsonObject)

        val videoTitle = videoSmallCard.title                                                       //视频标题
        val videoPlayUrl = videoSmallCard.playUrl                                                   //视频播放地址
        val videoFeedUrl = videoSmallCard.cover.detail                                              //视频封面Url
        val videoCategory = "#" + videoSmallCard.category                                           //视频类别
        val videoDuration = TimeUtil.getFormatHMS(videoSmallCard.duration * 1000.toLong())       //视频时长



        with(itemVideoSmallCardBinding!!) {

            //init view
            tvTitle.text = videoTitle
            tvCatogory.text = videoCategory
            tvVideoDuration.text = videoDuration
            ImageLoader.loadNetImageWithCorner(mContext, ivFeed, videoFeedUrl)

            //init Event
            holder.itemView.setOnClickListener { startVideoActivity(videoJson, position) }
        }
    }


    //没有更多数据，到底部的提示ItemView
    private fun initTheEndView(holder: CommonViewHolder, position: Int) {
        val itemTheEndBinding = DataBindingUtil.getBinding<ItemTheEndBinding>(holder.itemView)!!

        with(itemTheEndBinding) {
            tvEnd.typeface = Typeface.createFromAsset(mContext.assets, "fonts/Lobster-1.4.otf")
        }
    }


    //启动视频播放页面
    private fun startVideoActivity(videoJson: String, position: Int) {
        mContext.startActivity(
                Intent(mContext, VideoPlayActivity::class.java).apply {
                    putExtra("VIDEO_JSON", videoJson)
                    putExtra("JSON_TYPE", getItemViewType(position))
                }
        )
    }

}