(ns app.core
  (:require [clj-http.client :as client] [clojure.data.json :as json]))

(defn get-reddit-url
  "Gets new posts from a given subreddit with an optional after parameter"
  ([subreddit]
    (clojure.string/join ["http://www.reddit.com/r/" subreddit "/new.json?limit=1"]))

  ([subreddit before]
    (clojure.string/join [(get-reddit-url subreddit) "?before=" before]))
)

(defn get-data-from-reddit-api
  "Gets new posts from a given subreddit"
  [url]
  (println url)
  (:data
    (json/read-str
      (:body (client/get url)) :key-fn keyword))
)

(defn pause-for-reddit-api
  []
  (Thread/sleep 5000))

(defn get-next-reddit-post
  ([before]
    (pause-for-reddit-api)
    (if (nil? before)
      (get-data-from-reddit-api (get-reddit-url "clojure" ))
      (get-data-from-reddit-api (get-reddit-url "clojure" before))
  ))
)

(defn test-loop
  []
  (loop [result (get-next-reddit-post nil)]
    (println (-> result :children first :data :title))
    (println (-> result :children first :data :name))
    (recur (get-next-reddit-post (-> result :children first :data :name)))))


(defn -main
  "I don't do a whole lot."
  []
  (test-loop)
)

