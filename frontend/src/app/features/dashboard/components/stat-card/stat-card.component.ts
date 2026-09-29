import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stat-card.component.html',
  styleUrls: ['./stat-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StatCardComponent {
  @Input() title = '';
  @Input() value: number = 0;
  @Input() prefix = '';
  @Input() suffix = '';
  @Input() trend: 'up' | 'down' | 'neutral' = 'neutral';
  @Input() color = 'var(--primary)';

  get formattedValue(): string {
    if (this.suffix === '%') {
      return this.value.toFixed(1);
    }
    return this.value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }
}
