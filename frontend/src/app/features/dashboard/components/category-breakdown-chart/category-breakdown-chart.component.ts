import { Component, Input, OnChanges, SimpleChanges, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, LegendPosition, Color, ScaleType } from '@swimlane/ngx-charts';
import { CategoryBreakdown } from '../../../../core/models/dashboard.model';

@Component({
  selector: 'app-category-breakdown-chart',
  standalone: true,
  imports: [CommonModule, NgxChartsModule],
  templateUrl: './category-breakdown-chart.component.html',
  styleUrls: ['./category-breakdown-chart.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CategoryBreakdownChartComponent implements OnChanges {
  @Input() data: CategoryBreakdown[] = [];

  chartData: { name: string; value: number }[] = [];
  legendPosition = LegendPosition.Below;
  colorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#1a73e8', '#34a853', '#ea4335', '#fbbc04', '#9334e6', '#e8710a', '#1a9e8f', '#5f6368']
  };

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data'] && this.data) {
      this.chartData = this.data.map(item => ({
        name: item.categoryName,
        value: item.totalAmount
      }));
    }
  }
}
