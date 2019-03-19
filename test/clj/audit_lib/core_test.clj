(ns audit-lib.core-test
  (:require [clojure.test :refer :all]
            [audit-lib.core :refer :all]
            [mongo-lib.core :as mon]))

(def db-uri
     (or (System/getenv "MONGODB_URI")
         (System/getenv "PROD_MONGODB")
         "mongodb://admin:passw0rd@127.0.0.1:27017/admin"))

(def db-name
     "test-db")

(defn create-db
  "Create database for testing"
  []
  (mon/mongodb-connect
    db-uri
    db-name)
  (mon/mongodb-insert-one
    "user"
    { :username "test-admin"
      :email "test.123@123"
      :password "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3" })
  (let [test-user (mon/mongodb-find-one
                    "user"
                    {:username "test-admin"})]
    (mon/mongodb-insert-one
      "session"
      { :uuid "test-uuid"
        :user-agent "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:65.0) Gecko/20100101 Firefox/65.0"
        :user-id (:_id test-user)
        :username (:username test-user)
        :created-at (java.util.Date.) })
   ))

(defn destroy-db
  "Destroy testing database"
  []
  (mon/mongodb-delete-by-filter
    "audit"
    {})
  (mon/mongodb-delete-by-filter
    "session"
    {})
  (mon/mongodb-delete-by-filter
    "user"
    {})
  (mon/mongodb-disconnect))

(defn before-and-after-tests
  "Before and after tests"
  [f]
  (create-db)
  (f)
  (destroy-db))

(use-fixtures :once before-and-after-tests)

(deftest test-audit
  
  (testing "Test audit"
  
    (let [request {:cookie "session=test-uuid; session-visible=exists"
                   :request-uri "/test-uri"}
          response {:status "200 OK"}
          auditing (audit
                     request
                     response)
          is-audited (mon/mongodb-find-one
                       "audit"
                       {:action "/test-uri"
                        :body ""
                        :response "200 OK"})]
      (is
        (not
          (nil?
            is-audited))
       ))
  
    (let [request {:cookie "session=test-uuid; session-visible=exists"
                   :request-uri "/test-body-uri"
                   :body "Hello"}
          response {:status "200 OK"}
          auditing (audit
                     request
                     response)
          is-audited (mon/mongodb-find-one
                       "audit"
                       {:action "/test-body-uri"
                        :body "Hello"
                        :response "200 OK"})]
      (is
        (not
          (nil?
            is-audited))
       ))
  
    (let [request {:cookie "session=test-uuid; session-visible=exists"
                   :request-uri "/test-body-uri"
                   :body "test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456"}
          response {:status "200 OK"}
          auditing (audit
                     request
                     response)
          is-audited (mon/mongodb-find-one
                       "audit"
                       {:action "/test-body-uri"
                        :body "test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456"
                        :response "200 OK"})]
      (is
        (not
          (nil?
            is-audited))
       ))
  
    (let [request {:cookie "session=test-uuid; session-visible=exists"
                   :request-uri "/test-body-uri"
                   :body "test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456300"}
          response {:status "200 OK"}
          auditing (audit
                     request
                     response)
          is-audited (mon/mongodb-find-one
                       "audit"
                       {:action "/test-body-uri"
                        :body "test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456"
                        :response "200 OK"})]
      (is
        (not
          (nil?
            is-audited))
       ))
  
    (let [request {:cookie "session=test-uuid; session-visible=exists"
                   :request-uri "/test-body-cljmap-uri"
                   :body {:test 1}}
          response {:status "200 OK"}
          auditing (audit
                     request
                     response)
          is-audited (mon/mongodb-find-one
                       "audit"
                       {:action "/test-body-cljmap-uri"
                        :body "{:test 1}"
                        :response "200 OK"})]
      (is
        (not
          (nil?
            is-audited))
       ))
  
    (let [request {:cookie "session=test-uuid; session-visible=exists"
                   :request-uri "/test-body-cljmap-uri"
                   :body {:test "test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456300"}}
          response {:status "200 OK"}
          auditing (audit
                     request
                     response)
          is-audited (mon/mongodb-find-one
                       "audit"
                       {:action "/test-body-cljmap-uri"
                        :body "{:test \"test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456test123456te"
                        :response "200 OK"})]
      (is
        (not
          (nil?
            is-audited))
       ))

   ))

