(ns app.core
  (:require [foobar.reddit :as reddit] [foobar.irc :as irc]))

(defn format-post-for-irc
  [reddit-new-post-data]
  (format "new post on reddit ! \"%s\" click here to read -> http://www.reddit.com%s" (-> reddit-new-post-data :children first :data :title) (-> reddit-new-post-data :children first :data :permalink) )
  )

(defn get-new-post-loop
  [irc-client]
  (loop [result (reddit/get-next-reddit-post nil) last-known-name nil]
    (if-not (nil? (-> result :children first :data :name))
      (irc/irc-speak irc-client "#opinionlab" (format-post-for-irc result)))

    (recur (reddit/get-next-reddit-post (or (-> result :children first :data :name) last-known-name))
             (or (-> result :children first :data :name) last-known-name))))


(defn -main
  "Sets up IRC connection then starts reddit polling loop"
  []
  (let [irc-client (irc/connect irc/freenode)]
    (irc/login irc-client irc/user)
    (irc/write irc-client "JOIN #opinionlab")
    ;(irc/irc-speak irc-client "#testclojurebot" "test")
    (get-new-post-loop irc-client)
    ))

