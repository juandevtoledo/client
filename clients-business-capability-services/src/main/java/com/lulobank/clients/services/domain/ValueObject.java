package com.lulobank.clients.services.domain;

import com.lulobank.core.helpers.ObjectHelpers;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class ValueObject<T extends ValueObject<T>> {

  /**
   * Represents a list of fields that will be used to compare to other object.
   *
   * <p>So in the implementation, we need to populate with the getters of the fields that we need to
   * be used to compare to Value Objects
   */
  protected abstract List<Supplier> supplyGettersToIncludeInEqualityCheck();

  @Override
  public int hashCode() {
    return getHashCodeCore();
  }

  private int getHashCodeCore() {

    int hashCode = supplyGettersToIncludeInEqualityCheck().get(0).get().hashCode();

    for (int i = 1; i < supplyGettersToIncludeInEqualityCheck().size(); i++) {
      hashCode = (hashCode * 397) ^ supplyGettersToIncludeInEqualityCheck().get(i).get().hashCode();
    }
    return hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (ObjectHelpers.isNull(obj)) return false;
    if (supplyGettersToIncludeInEqualityCheck().isEmpty()) return false;
    try {
      T obj2 = (T) obj;
      for (int i = 0; i < supplyGettersToIncludeInEqualityCheck().size(); i++) {
        if (!Objects.equals(
            supplyGettersToIncludeInEqualityCheck().get(i).get(),
            obj2.supplyGettersToIncludeInEqualityCheck().get(i).get())) {
          return false;
        }
      }
      return true;
    } catch (ClassCastException e) {
      return false;
    }
  }
}
