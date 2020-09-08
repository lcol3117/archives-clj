(ns archives.core
  (:require [clojure.core.async
    :as a
    :refer [>! <! >!! <!! go chan buffer close! thread alts! alts!! timeout]]))

(defn archive [in-chan-args out-chan-args flow-map-given]
  (do
    (def in-chan (apply chan in-chan-args))
    (def out-chan (apply chan out-chan-args))
    (let [flow-map flow-map-given]
      (go-loop [_ nil]
        ; run archive process
        (recur nil)))
    {
      :in-chan in-chan
      :out-chan out-chan}))

(defn give!! [archive v]
  (>!! (archive :in-chan) {
                            :task :store
                            :data v}))

(defn retrieve!! [archive category]
  (let [
         t (thread
          (>!! (archive :in-chan) {
                                    :task :locate
                                    :data category})
          (<!! (archive :out-chan)))]
    (<!! t)))
