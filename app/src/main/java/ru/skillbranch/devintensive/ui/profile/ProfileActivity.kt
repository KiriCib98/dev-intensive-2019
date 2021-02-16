package ru.skillbranch.devintensive.ui.profile

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile_constraint.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.utils.Profile
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel


class ProfileActivity : AppCompatActivity() {

    companion object {
        //        const val SAVE_STATE_STATUS = "STATUS"
//        const val SAVE_STATE_QUESTION = "QUESTION"
        private const val IS_EDIT_MODE = "IS_EDIT_MODE"

    }

    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false

    lateinit var viewFields: Map<String, TextView>


    /**
     * Вызывается при первом создании или перезапуске Activity.
     * Здесь задается внешний вид активности (UI) через метод setContentView().
     * Инициализируются представления
     * Представления связываются с необходимыми данными и ресурсами
     * Связываются данные со списками
     *
     * Этот метод также предоставляет Bundle, содержащий ранее сохраненное состояние Activity, если оно было.
     *
     * Всегда сопровождается вызовом onStart().
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // set custom theme this before super and setContentView
//        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_constraint)
        initViews(savedInstanceState)
        initViewModel()
        Log.d("M_ProfileActivity", "onCreate")
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileDate().observe(this, Observer { updateUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
    }

    private fun updateTheme(theme: Int) {
        Log.d("M_ProfileActivity", "updateTheme $theme")
        delegate.setLocalNightMode(theme) // activity будет пересоздана
    }

    private fun updateUI(profile: Profile) {
        profile.toMap().also {
            for ((k, v) in viewFields) {
                v.text = it[k].toString()
            }
        }
        tv_nick_name.text = Utils.transliteration(profile.firstName + " " + profile.lastName, "_")
    }

    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
            "nickName" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )
        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        showCurrentMode(isEditMode)

        btn_edit.setOnClickListener {
            iv_avatar.setImageResource(0)
            if (isEditMode) saveProfileInfo()
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }

        et_repository.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (Utils.isValidRepositoryUrl(et_repository.text.toString())) {
                    wr_repository.error = ""
                    wr_repository.isErrorEnabled = false
                } else {
                    wr_repository.error = "Невалидный адрес репозитория"
                    wr_repository.isErrorEnabled = true
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

//        iv_avatar.setBorderColor("#ffffff")
//        iv_avatar.setBorderWidth(12)
    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter {
            setOf(
                "firstName",
                "lastName",
                "about",
                "repository"
            ).contains(it.key)
        }
        for ((_, v) in info) {
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if (isEdit) 255 else 0
        }
        ic_eye.visibility = if (isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit

        with(btn_edit) {
            val filter: ColorFilter? = if (isEdit) {
                PorterDuffColorFilter(
                    resources.getColor(R.color.color_accent, theme),
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                null
            }

            val icon = if (isEdit) {
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            } else {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }

            background.colorFilter = filter
            setImageDrawable(icon)
        }
    }

    /**
     * Если Activity возвращается в приоритетный режим после вызова onStop(), то в этом случае вызывается метод onRestart().
     * Т.е. вызывается после того, как Activity была остановлена и снова была запущена пользователем.
     * Всегда сопровождается вызовом метода onStart().
     *
     * Используется для специальных действий, которые должны выполняться только при повторном запуске Activity
     */
    override fun onRestart() {
        super.onRestart()
        Log.d("M_MainActivity", "onRestart")
    }


    /**
     * При вызове onStart() окно еще не видно пользователю, но вскоре будет видно.
     * Вызывается непосредственно перед тем, как активность становится видимой пользователю.
     *
     * Чтение из базы данных
     * Запуск сложной анимации
     * Запуск потоков, отслеживания показаний датчиков, запросов к GPS, таймеров, сервисов или других процессов,
     * которые нужны исключительно для обновления пользовательского интерфейса
     *
     * Затем следует onResume(), если Activity выходит на передний план
     */
    override fun onStart() {
        super.onStart()
        Log.d("M_MainActivity", "onStart")
    }


    /**
     * Вызывается, когда Activity начинает взаимодействовать с пользователемю
     *
     * Запуск воспроизведения анимации, аудио и видео
     * Ругистрации любых BroadcastReceiver или других процессов, которые вы освободили/приостановили в OnPause()
     * Выполнение любых других инициализаций,которые должны происходить, когда Activity вновь активна (камера).
     *
     * Тут должен быть максимально легкий и быстрый код, чтобы приложение оставалось отзывчивым
     */
    override fun onResume() {
        super.onResume()
        Log.d("M_MainActivity", "onResume")
    }


    /**
     * Метод onPause() вызывается после сворачивания текущей активности или перехода к новому.
     * От onPause() можно перейти к вызову onResume() или onStop().
     *
     *
     * Остановка анимации, аудио и видео
     * Сохранение состояния пользовательского ввода (легкие процессы)
     * Сохранение в DB, если данные должны быть доступны в новой Activity
     * Остановка сервисов, подписок, BroadcastReceiver
     *
     * Тут должен быть максимально легкий и быстрый код, чтобы приложение оставалось отзывчивым
     */
    override fun onPause() {
        super.onPause()
        Log.d("M_MainActivity", "onPause")
    }


    /**
     * Метод onPause() вызывается, когда Activity становится невидимой для пользователя.
     * Это может произойти при её уничтожении, или если была запущена другая Activity (существующая или новая),
     * перекрывшая окно текущей Activity.
     *
     * Запись в БД
     * Приостановка сложной анимации
     * Приостановка потоков, отслеживания показаний датчиков, запросов к GPS, таймеров, сервисов или других процессов,
     * которые нужны исключительно для обновления пользовательского интерфейса
     *
     * Не вызывается при вызове метода finish() у Activity
     */
    override fun onStop() {
        super.onStop()
        Log.d("M_MainActivity", "onStop")
    }


    /**
     * Метод вызывается по окончании работы Activity, при вызове метода finish() или в случае,
     * когда система уничтожает этот экземпляр активности для освобождения ресурсов.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d("M_MainActivity", "onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_EDIT_MODE, isEditMode)

//        outState.putString(SAVE_STATE_STATUS, benderObj.status.name)
//        outState.putString(SAVE_STATE_QUESTION, benderObj.question.name)
//        Log.d("M_MainActivity", "onSaveInstanceState ${benderObj.status.name} ${benderObj.question.name}")
    }

    private fun saveProfileInfo() {
        if (wr_repository.isErrorEnabled) {
            et_repository.setText("")
        }
        val firstName = et_first_name.text.toString()
        val lastName = et_last_name.text.toString()
        Profile(
            firstName = firstName,
            lastName = lastName,
            about = et_about.text.toString(),
            repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileDate(this)
        }
    }
}
