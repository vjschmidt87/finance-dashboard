import { Component, Input, OnChanges, SimpleChanges, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, LegendPosition, Color, ScaleType } from '@swimlane/ngx-charts';
import { MonthlySummary } from '../../../../core/models/dashboard.model';

@Component({
  selector: 'app-monthly-trend-chart',
  standalone: true,
  imports: [CommonModule, NgxChartsModule],
  templateUrl: './monthly-trend-chart.component.html',
  styleUrls: ['./monthly-trend-chart.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MonthlyTrendChartComponent implements OnChanges {
  @Input() data: MonthlySummary[] = [];

  chartData: { name: string; series: { name: string; value: number }[] }[] = [];
  legendPosition = LegendPosition.Below;
  colorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#34a853', '#ea4335']
  };

  private monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data'] && this.data) {
      this.chartData = this.data.map(item => ({
        name: this.monthNames[item.month - 1] + ' ' + item.year,
        series: [
          { name: 'Income', value: item.totalIncome },
          { name: 'Expenses', value: item.totalExpenses }
        ]
      }));
    }
  }
}
