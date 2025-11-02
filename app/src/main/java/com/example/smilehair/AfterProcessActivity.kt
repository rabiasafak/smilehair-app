package com.example.smilehair

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AfterProcessActivity : AppCompatActivity() {

    private val WHATSAPP_NUMBER = "+905551234567" // Buraya kliniğinizin numarasını yazın

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_process)

        setupToolbar()
        setupTreatmentCards()
        setupWhatsAppButtons()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupTreatmentCards() {
        findViewById<MaterialCardView>(R.id.sacEkimiCard).setOnClickListener {
            showTreatmentGuide(TreatmentType.SAC_EKIMI)
            selectCard(it as MaterialCardView)
        }

        findViewById<MaterialCardView>(R.id.sakalEkimiCard).setOnClickListener {
            showTreatmentGuide(TreatmentType.SAKAL_EKIMI)
            selectCard(it as MaterialCardView)
        }

        findViewById<MaterialCardView>(R.id.kasEkimiCard).setOnClickListener {
            showTreatmentGuide(TreatmentType.KAS_EKIMI)
            selectCard(it as MaterialCardView)
        }

        findViewById<MaterialCardView>(R.id.biyikEkimiCard).setOnClickListener {
            showTreatmentGuide(TreatmentType.BIYIK_EKIMI)
            selectCard(it as MaterialCardView)
        }
    }

    private fun selectCard(selectedCard: MaterialCardView) {
        listOf(R.id.sacEkimiCard, R.id.sakalEkimiCard, R.id.kasEkimiCard, R.id.biyikEkimiCard).forEach { id ->
            findViewById<MaterialCardView>(id).apply {
                strokeColor = Color.TRANSPARENT
                strokeWidth = 0
                cardElevation = 4f
            }
        }

        selectedCard.apply {
            strokeColor = Color.parseColor("#4CAF50")
            strokeWidth = 6
            cardElevation = 12f
        }
    }

    private fun setupWhatsAppButtons() {
        findViewById<MaterialCardView>(R.id.whatsappContactCard).setOnClickListener {
            openWhatsApp()
        }

        findViewById<FloatingActionButton>(R.id.floatingWhatsapp).setOnClickListener {
            openWhatsApp()
        }
    }

    private fun openWhatsApp() {
        val message = "Merhaba, işlem sonrası süreçle ilgili sorum var."
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$WHATSAPP_NUMBER&text=${Uri.encode(message)}")
        try {
            startActivity(intent)
        } catch (e: Exception) {
            intent.data = Uri.parse("https://wa.me/$WHATSAPP_NUMBER?text=${Uri.encode(message)}")
            startActivity(intent)
        }
    }

    private fun showTreatmentGuide(type: TreatmentType) {
        findViewById<LinearLayout>(R.id.contentContainer).visibility = View.VISIBLE
        findViewById<FloatingActionButton>(R.id.floatingWhatsapp).visibility = View.VISIBLE

        when (type) {
            TreatmentType.SAC_EKIMI -> showSacEkimiGuide()
            TreatmentType.SAKAL_EKIMI -> showSakalEkimiGuide()
            TreatmentType.KAS_EKIMI -> showKasEkimiGuide()
            TreatmentType.BIYIK_EKIMI -> showBiyikEkimiGuide()
        }

        findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView).post {
            findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView).smoothScrollTo(0, 600)
        }
    }

    private fun showSacEkimiGuide() {
        findViewById<TextView>(R.id.selectedTreatmentEmoji).text = "💇"
        findViewById<TextView>(R.id.selectedTreatmentTitle).text = "Saç Ekimi Sonrası Bakım"

        findViewById<TextView>(R.id.timelineText).text = """📅 İlk 3 Ay: Sabır süreci - Ekilen saçlar dökülebilir
📅 3-6 Ay: Yeni saçlar çıkmaya başlar
📅 6-12 Ay: Belirgin gelişme görülür
📅 12-18 Ay: Tam sonuç - Saçlar tamamen uzar"""

        findViewById<TextView>(R.id.mainContentText).text = """Saç ekim işleminiz başarılı bir şekilde gerçekleşti! 🎉

Ekilen saçların bölgeye tam uyum sağlaması ve uzaması için 12-18 aya ihtiyacınız olacak. İlk 3 ay özellikle önemli - bu dönemde ekilen saçların döküldüğünü görebilirsiniz, bu tamamen normaldir.

⚠️ ÖNEMLİ: Başarılı bir ekim sürecinin %50'si operasyon, diğer %50'si ise operasyon sonrası bakımdır!

Operasyon sonrası iyi bakılmayan saç köklerinin büyümemesi, memnuniyetsizliklerin en büyük nedenidir. Bu yüzden aşağıdaki önerilere mutlaka uymanız gerekiyor.

Smile Hair Clinic olarak size operasyon sonrası bakım çantası ve detaylı bir kitapçık sunduk. Ayrıca her zaman yanınızdayız - herhangi bir sorunuz olduğunda bizimle iletişime geçmekten çekinmeyin! 💚"""

        populateTips(listOf(
            "🚭 Olabildiğince az sigara için - Kan dolaşımı iyileşme için kritik",
            "🥗 Doğal ve faydalı besinler tüketin - Saç sağlığı içeriden başlar",
            "🧴 Kimyasal şampuanlardan kaçının - Doğal ürünleri tercih edin",
            "💊 Doktorunuzun önerdiği vitaminleri düzenli kullanın",
            "💆 Besleyici bakım ürünlerini aksatmayın",
            "😴 Yeterli uyku alın - Hücre yenilenmesi için gerekli",
            "💧 Bol su için - Hidratasyon iyileşmeyi hızlandırır",
            "☀️ İlk 3 ay direkt güneşten kaçının",
            "🏃 Ağır sporlardan ilk 2 hafta uzak durun",
            "📱 Sorularınız için mutlaka doktorunuzla iletişimde kalın"
        ))
    }

    private fun showSakalEkimiGuide() {
        findViewById<TextView>(R.id.selectedTreatmentEmoji).text = "🧔"
        findViewById<TextView>(R.id.selectedTreatmentTitle).text = "Sakal Ekimi Sonrası Bakım"

        findViewById<TextView>(R.id.timelineText).text = """📅 İlk 2-3 Gün: Şişlik ve kızarıklık (normal)
📅 1. Hafta: İyileşme tamamlanır, işe dönüş
📅 2-3. Hafta: Sakallar şekillenmeye başlar
📅 3. Hafta sonrası: Geçici dökülme (%50-80 normal)
📅 3-4. Ay: Yeni sakallar çıkmaya başlar
📅 6-12 Ay: Tam sonuç - Doğal sakal görünümü"""

        findViewById<TextView>(R.id.mainContentText).text = """Sakal ekimi işleminiz başarıyla tamamlandı! 🎉

İyileşme süreci oldukça hızlıdır ve gözle görülür büyük bir yara izi kalmaz. Ciltte kızarma, şişlik ve kuruluk gibi geçici belirtiler birkaç gün içinde tamamen ortadan kalkar.

⚠️ İLK 2-3 GÜN: Yüz hassas bir bölge olduğu için şişkinlik yaşanabilir. İlaç kullanımı ve buz uygulamalarıyla bu semptomlar minimal düzeye indirgenir.

😴 UYKU POZİSYONU ÇOK ÖNEMLİ: Ekili graftlere zarar vermemek için uygun pozisyonda yatmanız gerekir.

💼 İŞE DÖNÜŞ: Medikal olarak 2-3 gün sonra dönebilirsiniz, ancak görünüş açısından 1 hafta beklemek daha idealdir.

📉 DÖKÜLME DÖNEMİ: 2-3. hafta sonrası ekilen köklerin %50-80'i geçici olarak dökülür - PANİK YAPMAYIN, bu tamamen normal! 3-4. ayda tekrar çıkmaya başlarlar.

📱 Herhangi bir endişeniz varsa mutlaka doktorunuzla iletişime geçin!"""

        populateTips(listOf(
            "❄️ İlk 2-3 gün düzenli buz uygulayın - Şişliği azaltır",
            "💊 Doktorunuzun verdiği ilaçları aksatmadan kullanın",
            "😴 Sırt üstü yatın - Ekili bölgeye baskı yapmayın",
            "💧 Ekili bölgeyi ilk 3 gün ıslatmayın",
            "🚿 İlk yıkama için doktorunuzun talimatlarını bekleyin",
            "🧴 Sadece önerilen bakım ürünlerini kullanın",
            "🚭 Sigara içmeyin - İyileşmeyi geciktirir",
            "🍺 Alkol tüketmeyin - Şişliği artırabilir",
            "☀️ Direkt güneşe maruz kalmayın",
            "📱 Değişiklikler olursa hemen doktorunuzu arayın"
        ))
    }

    private fun showKasEkimiGuide() {
        findViewById<TextView>(R.id.selectedTreatmentEmoji).text = "👁️"
        findViewById<TextView>(R.id.selectedTreatmentTitle).text = "Kaş Ekimi Sonrası Bakım"

        findViewById<TextView>(R.id.timelineText).text = """📅 İlk 2-3 Gün: Hafif şişlik ve kızarıklık
📅 1. Hafta: İyileşme süreci tamamlanır
📅 2-3. Hafta: Kaşlar şekil almaya başlar
📅 1. Ay sonrası: Geçici dökülme dönemi
📅 3-4. Ay: Yeni kaşlar çıkmaya başlar
📅 6-12 Ay: Tam sonuç - Doğal kaş görünümü"""

        findViewById<TextView>(R.id.mainContentText).text = """Kaş ekimi işleminiz başarıyla gerçekleşti! 🎉

Kaş bölgesi oldukça hassastır ve özel bakım gerektirir. İyileşme süreci hızlıdır ancak dikkatli olmanız önemlidir.

⚠️ ÇOK ÖNEMLİ: Kaş bölgesi göz çevresinde olduğu için ekstra hassasiyet göstermeniz gerekir. Ekili bölgeye dokunmamak, kaşımamak ve ovmamak kritik önemde!

😴 UYKU POZİSYONU: Yüzünüzü yastığa bastırmadan sırt üstü yatmaya özen gösterin.

💄 MAKİYAJ: İlk 2 hafta kaş bölgesine makyaj yapmaktan kaçının.

📉 DÖKÜLME DÖNEMİ: 1. ay sonrası ekilen kaşların çoğu geçici olarak dökülür - endişelenmeyin, bu doğal bir süreçtir! 3-4. ayda tekrar çıkmaya başlarlar.

📱 Sorularınız için doktorunuzla sürekli iletişimde olun!"""

        populateTips(listOf(
            "🚫 Ekili bölgeye kesinlikle dokunmayın, kaşımayın",
            "💧 İlk 3 gün kaş bölgesini ıslatmayın",
            "🚿 Yıkama talimatlarını doktorunuzdan alın",
            "😴 Sırt üstü uyuyun - Yüzünüzü yastığa bastırmayın",
            "💄 İlk 2 hafta kaş makyajı yapmayın",
            "🧴 Sadece önerilen bakım ürünlerini kullanın",
            "☀️ Güneş gözlüğü takın - Ekili bölgeyi koruyun",
            "🏊 İlk ay havuz ve denize girmeyin",
            "💆 Cilt bakımı yapılacaksa kaş bölgesinden uzak durun",
            "📱 Her türlü soru için doktorunuzla iletişime geçin"
        ))
    }

    private fun showBiyikEkimiGuide() {
        findViewById<TextView>(R.id.selectedTreatmentEmoji).text = "👨"
        findViewById<TextView>(R.id.selectedTreatmentTitle).text = "Bıyık Ekimi Sonrası Bakım"

        findViewById<TextView>(R.id.timelineText).text = """📅 İlk 2-3 Gün: Hafif şişlik ve kızarıklık
📅 1. Hafta: İyileşme tamamlanır
📅 2-3. Hafta: Bıyıklar şekillenmeye başlar
📅 3. Hafta sonrası: Geçici dökülme dönemi
📅 3-4. Ay: Yeni bıyıklar çıkmaya başlar
📅 6-12 Ay: Tam sonuç - Doğal bıyık görünümü"""

        findViewById<TextView>(R.id.mainContentText).text = """Bıyık ekimi işleminiz başarıyla tamamlandı! 🎉

İyileşme süreci hızlı olup, birkaç gün içinde normal yaşamınıza dönebilirsiniz. Dudak üstü bölge hassas olduğu için özel dikkat gerektirir.

⚠️ DİKKAT: Dudak ve burun hareketleri ekili bölgeyi etkileyebilir. İlk günlerde mümkün olduğunca yüz hareketlerini minimal tutun.

🍽️ BESLENME: İlk günlerde sıcak, baharatlı ve sert yiyeceklerden kaçının. Ekili bölgeye temas edebilecek yiyeceklerle dikkatli olun.

😷 HİJYEN: Ağız hijyenine özen gösterin ama ekili bölgeye zarar vermemeye dikkat edin.

📉 DÖKÜLME DÖNEMİ: 2-3. hafta sonrası ekilen bıyıkların çoğu geçici olarak dökülür - bu tamamen normaldir! 3-4. ayda tekrar çıkmaya başlarlar.

📱 Endişeleriniz için doktorunuzla iletişime geçmekten çekinmeyin!"""

        populateTips(listOf(
            "🍽️ İlk günlerde yumuşak besinler tüketin",
            "🌶️ Baharatlı ve sıcak yiyeceklerden kaçının",
            "💧 İlk 3 gün bıyık bölgesini ıslatmayın",
            "🪥 Ağız hijyenine dikkat edin ama ekili bölgeye zarar vermeyin",
            "😴 Sırt üstü yatın - Yüzünüzü yastığa bastırmayın",
            "🧴 Sadece önerilen bakım ürünlerini kullanın",
            "🚭 Sigara içmeyin - İyileşmeyi engeller",
            "😁 İlk günlerde aşırı yüz hareketlerinden kaçının",
            "☀️ Direkt güneşe maruz kalmayın",
            "📱 Soru ve endişeleriniz için doktorunuzu arayın"
        ))
    }

    private fun populateTips(tips: List<String>) {
        val container = findViewById<LinearLayout>(R.id.tipsContainer)
        container.removeAllViews()

        tips.forEach { tipText ->
            val tipView = TextView(this).apply {
                text = tipText
                textSize = 15f
                setTextColor(Color.parseColor("#424242"))
                setPadding(0, 0, 0, (24 * resources.displayMetrics.density).toInt())
                setLineSpacing((6 * resources.displayMetrics.density), 1f)
            }
            container.addView(tipView)
        }
    }

    enum class TreatmentType {
        SAC_EKIMI, SAKAL_EKIMI, KAS_EKIMI, BIYIK_EKIMI
    }
}