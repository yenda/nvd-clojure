(ns nvd.task.check-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [nvd.task.check :as sut])
  (:import
   (org.owasp.dependencycheck.data.nvdcve DatabaseException)
   (org.owasp.dependencycheck.data.update.exception UpdateException)))

(deftest update-failures-only?
  (testing "a non-fatal collection of update failures still allows a report"
    (is (sut/update-failures-only? false [(UpdateException. "feed download cut")]))
    (is (sut/update-failures-only? false [(UpdateException. "a")
                                          (UpdateException. "b")])))

  (testing "a fatal collection never does"
    (is (not (sut/update-failures-only? true [(UpdateException. "feed download cut")]))))

  (testing "any other error never does"
    (is (not (sut/update-failures-only? false [(UpdateException. "feed download cut")
                                               (DatabaseException. "no database")])))
    (is (not (sut/update-failures-only? false [(IllegalStateException. "analyzer failed")]))))

  (testing "an empty collection is not an update failure"
    (is (not (sut/update-failures-only? false [])))))
