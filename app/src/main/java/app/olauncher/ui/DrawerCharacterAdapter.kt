package app.olauncher.ui

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import app.olauncher.R
import app.olauncher.data.Constants.CharacterIndicator
import app.olauncher.data.DrawerCharacterModel
import app.olauncher.databinding.DrawerAlphabetBinding
import app.olauncher.helper.getColorFromAttr

class DrawerCharacterAdapter : ListAdapter<DrawerCharacterModel, DrawerCharacterAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = DrawerAlphabetBinding.bind(view)
        fun bind(character: DrawerCharacterModel) {

            val textColor = if (character.inRange)
                itemView.context.getColor(R.color.colorAccent)
            else
                itemView.context.getColorFromAttr(R.attr.primaryColor)
            binding.character.setTextColor(textColor)
            binding.character.text = character.character
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.drawer_alphabet, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<DrawerCharacterModel>() {
            override fun areItemsTheSame(
                oldItem: DrawerCharacterModel,
                newItem: DrawerCharacterModel,
            ): Boolean = oldItem.character == newItem.character

            override fun areContentsTheSame(
                oldItem: DrawerCharacterModel,
                newItem: DrawerCharacterModel,
            ): Boolean = oldItem.hashCode() == newItem.hashCode()
        }
    }

    class CharacterTouchListener(
        private val adapter: DrawerCharacterAdapter,
        private val clickListener: ((String, Int, Pair<Float, Float>) -> Unit)?,
    ) : OnItemTouchListener {

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            updateIndicatorView(rv, e)
            return true
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = updateIndicatorView(rv, e)

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

        private fun updateIndicatorView(rv: RecyclerView, e: MotionEvent) {
            val child = rv.findChildViewUnder(e.x, e.y)
            child?.let {
                val view = rv.getChildViewHolder(child).itemView
                val itemPosition = rv.getChildAdapterPosition(child)

                clickListener?.let {
                    it(
                        adapter.currentList[itemPosition].character,
                        CharacterIndicator.SHOW,
                        Pair(view.x, view.y)
                    )
                }
                if (e.action == MotionEvent.ACTION_UP)
                    clickListener?.let { it("", CharacterIndicator.HIDE, Pair(e.x, e.y)) }
            } ?: clickListener?.let { it("", CharacterIndicator.HIDE, Pair(e.x, e.y)) }
        }
    }
}

