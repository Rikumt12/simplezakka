package com.example.simplezakka.config;

import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Autowired
    public DataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        loadSampleProducts();
    }

    private void loadSampleProducts() {
        if (productRepository.count() > 0) {
            return; 
        }

        List<Product> products = Arrays.asList(
            createProduct(
                "シンプルデスクオーガナイザー", 
                "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 
                3500, 
                20, 
                "/images/desk-organizer.png", 
                true,
                "デスク周辺"
            ),
            createProduct(
                "アロマディフューザー（ウッド）", 
                "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 
                4200, 
                15, 
                "/images/aroma-diffuser.png", 
                true,
                "リビングインテリア"
            ),
            createProduct(
                "コットンブランケット", 
                "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 
                5800, 
                10, 
                "/images/cotton-blanket.png", 
                false,
                "リビングインテリア"
            ),
            createProduct(
                "ステンレスタンブラー", 
                "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。", 
                2800, 
                30, 
                "/images/tumbler.png", 
                false,
                "キッチン"
            ),
            createProduct(
                "ミニマルウォールクロック", 
                "余計な装飾のないシンプルな壁掛け時計。静音設計！", 
                3200, 
                25, 
                "/images/wall-clock.png", 
                false,
                "リビングインテリア"
            ),
            createProduct(
                "リネンクッションカバー", 
                "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。", 
                2500, 
                40, 
                "/images/cushion-cover.png", 
                true,
                "リビングインテリア"
            ),
            createProduct(
                "陶器フラワーベース", 
                "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。", 
                4000, 
                15, ×
                "/images/flower-vase.png", 
                false,
                "リビングインテリア"
            ),
            createProduct(
                "木製コースター（4枚セット）", 
                "天然木を使用したシンプルなデザインのコースター。4枚セット。", 
                1800, 
                50, 
                "/images/wooden-coaster.png", 
                false,
                "キッチン"
            ),
            createProduct(
                "キャンバストートバッグ", 
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。", 
                3600, 
                35, 
                "/images/tote-bag.png", 
                true,
                "おでかけ"
            ),
             createProduct(
                "ソープディスペンサー", 
                "シンプルで洗練されたデザインのソープディスペンサー。", 
                1800, 
                35, 
                "/images/sopedispenser.png", 
                true,
                "キッチン"
            ),
             createProduct(
                "珪藻土バスマット", 
                "水分を素早く吸収する珪藻土素材のバスマット。お手入れも簡単。", 
                1500, 
                35, 
                "/images/keisoudo.png", 
                true,
                "キッチン"
            ),
             createProduct(
                "ベーシックノート", 
                "書き心地の良い紙を使用したシンプルなノート。日常使いに最適。", 
                500, 
                35, 
                "/images/note.png", 
                true,
                "デスク周辺"     
            ),
            createProduct(
                "ペン立て(木製)", 
                "天然木を使用したナチュラルな雰囲気のペン立て。デスク周りをすっきり整理。", 
                2500, 
                35, 
                 "/images/penntate.png", 
                true,
                "リビングインテリア"
            ),
             createProduct(
                "カレンダー（2025）", 
                "シンプルで見やすいレイアウトの2025年版カレンダー。", 
                900, 
                35, 
                "/images/karennda-.png", 
                true,
                "リビングインテリア"
            ),
             createProduct(
                "ウォーターボトル", 
                "軽量で持ち運びやすいウォーターボトル。おでかけにぴったり。", 
                2300, 
                35, 
                "/images/suitou.png", 
                true,
                "おでかけ"
            ),
             createProduct(
                "折りたたみかさ", 
                "コンパクトに折りたためるシンプルな傘。突然の雨にも安心。", 
                3200, 
                35, 
                "/images/kasa.jpg", 
                true,
                "おでかけ"
            ),
            createProduct(
                "ガラス保存容器セット", 
                "電子レンジ・食洗機対応のガラス製保存容器。3サイズセット。", 
                1300, 
                20, 
                "/images/glass-container.png", 
                false,
                "キッチン"
            )
        );
        
        productRepository.saveAll(products);
    }
    
    private Product createProduct(String name, String description, Integer price, Integer stock, String imageUrl, Boolean isRecommended,String category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        product.setIsRecommended(isRecommended);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }
}