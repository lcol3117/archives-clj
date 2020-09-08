(ns archives.core
  (:require [clojure.core.async
    :as a
    :refer [>! <! >!! <!! go chan buffer close! thread alts! alts!! timeout]]))

(defn archive [in-chan-args out-chan-args flow-map]
  (do
    (def in-chan (apply chan in-chan-args))
    (def out-chan-map (into (hash-map)
      (map (fn [[key_ _]] [key_ (apply chan out-chan-args)]) (assoc flow-map :else #(true)))))
    (go-loop [_ nil]
      ; run archive process
      (recur nil))
    {
      :in-chan in-chan
      :out-chan-map out-chan-map}))

(defn give!! [archive v]
  (go
    (>!! (archive :in-chan) v)))

(defn retrieve!! [archive category]
  (<!! ((archive :out-chan-map) category)))
