package com.eunsun.travel_mate.domain;

import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "favorite")
public class Favorite extends Base {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long favoriteId;

  @ManyToOne
  @JoinColumn(name="user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "tour_info_id", nullable = false)
  private TourInfo tourInfo;

}
