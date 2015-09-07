(ns aatree.nodes
(:import (clojure.lang Counted RT Indexed)
         (java.util Iterator Comparator)
         (aatree CountedSequence)))


(defprotocol INode
  (new-node [this t2 ^int level left right ^int cnt])
  (revise [this args])
  (skew [this])
  (split [this])
  (predecessor-t2 [this])
  (successor-t2 [this])
  (decrease-level [this])
  (nth-t2 [this ^int i]))

(defn empty-node? [n]
  (or (nil? n) (zero? (.-level n))))

(defn first-t2 [this]
  (cond
    (empty-node? this) nil
    (empty-node? (.-left this)) (.-t2 this)
    :else (recur (.-left this))))

(defn last-t2 [this]
  (cond
    (empty-node? this) nil
    (empty-node? (.-right this)) (.-t2 this)
    :else (recur (.-right this))))

(defn empty-node [this]
  (if (empty-node? this)
    this
    (.-nada this)))

(defn left-node [this]
  (if (empty-node? (.-left this))
    (empty-node this)
    (.-left this)))

(defn right-node [this]
  (if (empty-node? (.-right this))
    (empty-node this)
    (.-right this)))

(deftype counted-iterator
  [node
   ^{:volatile-mutable true int true} ndx
   ^int cnt]

  Counted
  (count [this] (- cnt ndx))

  Iterator
  (hasNext [this]
    (< ndx cnt))
  (next [this]
    (let [i ndx]
      (set! ndx (+ 1 i))
      (.nth-t2 node i))))

(defn ^counted-iterator new-counted-iterator
  ([node]
   (->counted-iterator node 0 (.-cnt node)))
  )

(defn ^CountedSequence new-counted-seq
  ([node]
   (CountedSequence/create (new-counted-iterator node) identity)))

(deftype counted-reverse-iterator
  [node
   ^{:volatile-mutable true int true} ndx]

  Counted
  (count [this] (+ 1 ndx))

  Iterator
  (hasNext [this]
    (>= ndx 0))
  (next [this]
    (let [i ndx]
      (set! ndx (- i 1))
      (.nth-t2 node i))))

(defn ^counted-reverse-iterator new-counted-reverse-iterator
  ([node]
   (->counted-reverse-iterator node (- (.-cnt node) 1)))
  )

(defn ^CountedSequence new-counted-reverse-seq
  ([node]
   (CountedSequence/create (new-counted-reverse-iterator node) identity)))

(defn snodev [this]
  (if (empty-node? this)
    ""
    (str (snodev (.-left this)) " <" (.-t2 this) " " (.level this) "> " (snodev (.-right this)))))

(defn pnodev [this dsc]
  (println dsc (snodev this)))
