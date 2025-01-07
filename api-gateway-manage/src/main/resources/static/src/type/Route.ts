import {jsonPretty} from "@/utils/Utils";

export class RouteTemplate {
  tid: number;
  name: string;
  uri: string;
  predicates: string;
  filters: string;
  order: number;

  constructor(tid: number, name:string, uri: string, predicates: string, filters: string, order: number) {
    this.tid = tid;
    this.name=name;
    this.uri = uri;
    this.predicates = predicates;
    this.filters = filters;
    this.order = order;
  }

  copy(t: RouteTemplate): void {
    this.tid = t.tid;
    this.name=t.name;
    this.uri = t.uri;
    this.predicates = t.predicates;
    this.filters = t.filters;
    this.order = t.order;
  }
}

export class RouteInfo {
  rid: string;
  uri: string;
  predicates: string;
  filters: string;
  order: number;
  enabled: string;
  tempId: number;

  constructor(rid: string, uri: string, predicates: string, filters: string, order: number, enabled: string, tempId: number) {
    this.rid = rid;
    this.uri = uri;
    this.predicates = predicates;
    this.filters = filters;
    this.order = order;
    this.enabled = enabled;
    this.tempId = tempId;
  }

  setUseTemp(t: RouteTemplate): void {
    this.uri = t.uri;
    this.predicates = jsonPretty(t.predicates);
    this.filters = jsonPretty(t.filters);
    this.order = t.order;
    this.tempId = t.tid;
  }

  toRestfulObj(): any {
    return {
      id: this.rid,
      uri: this.uri,
      predicates: JSON.parse(this.predicates),
      filters: JSON.parse(this.filters),
      order: this.order,
      enabled: this.enabled,
      tempId: this.tempId
    }
  }

  copy(obj: any): void {
    this.rid = obj.id;
    this.uri = obj.uri;
    this.predicates = jsonPretty(obj.predicates);
    this.filters = jsonPretty(obj.filters);
    this.order = obj.order;
    this.enabled = obj.enabled;
    this.tempId = obj.tempId;
  }
}
