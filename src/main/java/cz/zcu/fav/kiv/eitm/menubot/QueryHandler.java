package cz.zcu.fav.kiv.eitm.menubot;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryHandler {

    private Map<String, Restaurant> restaurantMap;
    private Directory index;

    public QueryHandler() throws IOException {
        this.restaurantMap = new HashMap<>();
        org.jsoup.nodes.Document doc = Jsoup.connect("https://www.menicka.cz/plzen.html").get();
        Elements restaurants = doc.select("div.menicka_detail");
        Elements names = restaurants.select("div.nazev");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < restaurants.size(); i++) {
            var restaurant = restaurants.get(i);
            var name = names.get(i).child(0).text();

            Elements meals = restaurant.select("div.nabidka_1");
            if (meals.size() <= 1)
                continue;
            Elements prices = restaurant.select("div.cena");

            int menuSize = meals.size();
            var r = new Restaurant(name, menuSize);

            for (int j = 0; j < meals.size(); j++) {
                if (j >= prices.size())
                    r.menu.add(meals.get(j).text());
                else
                    r.menu.add(meals.get(j).text() + " " + prices.get(j).text());
            }

            r.createMenuString(sb);
            restaurantMap.put(r.name, r);
        }

        Analyzer analyzer = new CzechAnalyzer();
        index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);

        for (var entry : restaurantMap.entrySet()) {
            Document d = new Document();
            d.add(new TextField("name", entry.getValue().name, Field.Store.YES));
            d.add(new TextField("menu", entry.getValue().menuString, Field.Store.NO));
            w.addDocument(d);
        }
        w.close();
    }

    public List<Restaurant> queryFood(String food) {
        List<Restaurant> restaurants = new ArrayList<>();
        try {
            Query q = createQuery(food, "menu", BooleanClause.Occur.MUST);

            int hitsPerPage = 10;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                restaurants.add(this.restaurantMap.get(d.get("name")));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return restaurants;
    }

    public Restaurant queryRestaurant(String restaurantName) {
        Query q = createQuery(restaurantName, "name", BooleanClause.Occur.SHOULD);
        Restaurant restaurant = null;
        try {
            int hitsPerPage = 1;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            if (hits.length >= 1) {
                int docId = hits[0].doc;
                Document d = searcher.doc(docId);
                restaurant = this.restaurantMap.getOrDefault(d.get("name"), null);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return restaurant;
    }

    private Query createQuery(String q, String field, BooleanClause.Occur occur) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        String[] arr = q.split(" ");
        for (var str : arr) {
            if (str.length() == 1)
                continue;
            builder.add(new FuzzyQuery(new Term(field, str)), occur);
        }

        return builder.build();
    }
}
