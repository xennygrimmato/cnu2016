from django.shortcuts import render
from django.http import JsonResponse
from models import *
import datetime
# Create your views here.
from django.db.models import *
from django.views.decorators.csrf import csrf_exempt

from rest_framework import viewsets, status
from rest_framework.response import Response
from serializers import *
from rest_framework import mixins
from django.http import HttpResponse

@csrf_exempt
def func(request):
    if 'startDate' in request.GET:
        startDate = request.GET['startDate']
    else:
        startDate = "01/01/1999"

    if 'endDate' in request.GET:
        endDate = request.GET['endDate']
    else:
        endDate = "31/12/2020"

    startDate = "-".join(x for x in startDate.split("/"))
    endDate = "-".join(x for x in endDate.split("/"))

    start_date = datetime.datetime.strptime(startDate, "%m-%d-%Y").date()
    end_date = datetime.datetime.strptime(endDate, "%m-%d-%Y").date()


    #ret = models.Orders.objects.filter(amount__gte=5000)
    #ret = models.OrderProduct2.objects.filter(order__amount__gte=6000)
    ret = models.OrderProduct2.objects.filter(order__timestamp__gte=start_date, order__timestamp__lte=end_date).extra({'date':"date(timestamp)"}).values('date').order_by('-date').annotate(orders=Count('id'), qty=Sum('quantity'), buy_price=Sum(F('quantity') * F('buying_cost'), output_field=DecimalField(max_digits=10, decimal_places=4)), sale_price=Sum(F('quantity') * F('selling_cost'), output_field=DecimalField(max_digits=10, decimal_places=4)), profit=Sum(F('quantity') * F('selling_cost'), output_field=DecimalField(max_digits=10, decimal_places=4))-Sum(F('quantity') * F('buying_cost'), output_field=DecimalField(max_digits=10, decimal_places=4)))

    lst = [obj for obj in ret]

    for obj in lst:
        tmp = obj["date"].strftime("%Y-%m-%d").split("-")
        obj["date"] = tmp[1] + "/" + tmp[2] + "/" + tmp[0]
        obj["buy_price"] = float(obj["buy_price"])
        obj["sale_price"] = float(obj["sale_price"])
        obj["profit"] = float(obj["profit"])

    return JsonResponse({
                         "data": lst
                        })

class CategoryViewSet(viewsets.ModelViewSet):
    queryset = Category.objects.all()
    serializer_class = CategorySerializer

class ProductViewSet(viewsets.ModelViewSet):
    queryset = Product.objects.filter(Q(deleted=0)|Q(deleted=None)) # pass this only for GET, PUT, PATCH requests, not for DELETE
    serializer_class = ProductSerializer

    def destroy(self, request, *args, **kwargs):
        try:
            instance = self.get_object()
            instance.deleted = 1
            instance.save()
        except:
            return Response(status=status.HTTP_404_NOT_FOUND)
        return Response(status=status.HTTP_204_NO_CONTENT)

class CategoryProductViewSet(viewsets.ModelViewSet):
    queryset = CategoryProduct.objects.all()
    serializer_class = CategoryProductSerializer

class OrderViewSet(viewsets.ModelViewSet):
    queryset = Orders.objects.all()
    serializer_class = OrderSerializer

    def create(self, request, *args, **kwargs):
        orderSerializer = OrderSerializer(data=request.data)
        data = request.data

        if orderSerializer.is_valid():
            username = validated_data['uid']['company_name'] if 'company_name' in validated_data['uid'] else None
            address = validated_data['uid']['addr_line1'] if 'addr_line1' in validated_data['uid'] else None
            status_str = data['status']

            # if username is not None:
            #       Create User if not exists
            # else get user
            if username:
                try:
                    # maybe, a better functionality would be to get user based on name as well as address
                    user = User.objects.get(company_name=username)
                except:
                    user = User(company_name=username, addr_line1=address)
                    user.save()
            else:
                user = User.objects.get_or_create(company_name="")[0]

            if status_str.lower() == "created":
                try:
                    order = Orders(uid=user, status=status_str, timestamp=datetime.datetime.now())
                    order.save()
                except:
                    # there was some problem with save
                    return Response({}, status=status.HTTP_500_INTERNAL_SERVER_ERRORs)
            else:
                order = Orders(uid=user, status=status_str)
                order.save()

            serializer = OrderSerializer(order)
            return Response(serializer.data, status=status.HTTP_201_CREATED)

        else:
            return Response(None, status=status.HTTP_400_BAD_REQUEST)



class OrderLineViewSet(mixins.RetrieveModelMixin, mixins.CreateModelMixin, mixins.ListModelMixin, viewsets.GenericViewSet):
    queryset = OrderProduct2.objects.filter(order__deleted=0)
    serializer_class = OrderLineItemSerializer

    def create(self, request, *args, **kwargs):
        orderLineItemSerializer = OrderLineItemSerializer(data=request.data)
        data = request.data
        if orderLineItemSerializer.is_valid():
            product = Product.objects.get(id=int(data['product_id']))
            order = Orders.objects.get(oid=int(kwargs['order_id']))
            try:
                order_product = OrderProduct2.objects.get(product=product, order=order)
                order_product.selling_cost = round(float(data['price']), 4)
                order_product.save()
            except:
                order_product = OrderProduct2(product=product, order=order, buying_cost=round(float(data['price']), 4))
                order_product.save()
            serializer = OrderLineItemSerializer(order_product)
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        else:
            return Response(None, status=status.HTTP_400_BAD_REQUEST)

    def retrieve(self, request, *args, **kwargs):
        try:
            obj = OrderProduct2.objects.get(order__oid=kwargs['order_id'], product__id=kwargs['pk'])
            serializer= OrderLineItemSerializer(obj, many=False)
            return Response(serializer.data, status=status.HTTP_200_OK)
        except:
            return Response({}, status=status.HTTP_404_NOT_FOUND)

    def list(self, request, *args, **kwargs):
        queryset = OrderProduct2.objects.filter(order__oid=kwargs['order_id'])
        serializer= OrderLineItemSerializer(queryset, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)
        #return HttpResponse(serialize('json', queryset=queryset))

def order_summary_view(request):

    group_by = False
    if "group_by" in request.GET:
        group_by = True
        group_by_field = request.GET["group_by"]

    product_code_filter = False
    if "orderlineitem__product__code" in request.GET:
        product_code_filter = True
        product_code = request.GET["orderlineitem__product__code"]

    category_name_filter = False
    if "orderlineitem__product__category__name" in request.GET:
        category_name_filter = True
        category_name = request.GET["orderlineitem__product__category__name"]




    if product_code_filter and not category_name_filter:
        orders = OrderProduct2.objects.filter(product__code=product_code)\
                                      .exclude(order__deleted=1)

    elif category_name_filter and not product_code_filter:
        orders = OrderProduct2.objects.filter(product__category__name=category_name)\
                                      .exclude(order__deleted=1)

    elif category_name_filter and product_code_filter:
        orders = OrderProduct2.objects.filter(product__category__name=category_name, product__code=product_code)\
                                      .exclude(order__deleted=1)

    else:
        orders = OrderProduct2.objects.filter().exclude(order__deleted=1)

    # If "group_by" query parameter exists, we must group by the appropriate entity
    if group_by:
        with open('/Users/vaibhavtulsyan/as6/a6/tmp.txt', 'w') as f:
            f.write('Here')
        if group_by_field=="category":
            orders = orders.annotate(category_id=F('product__category__id'))\
                           .values('product__category__id')\
                           .annotate(count=Count('product__category__id'))\
                           .exclude(count__lte=0)

            with open('/Users/vaibhavtulsyan/as6/a6/tmp.txt', 'w') as f:
                f.write(str(list(orders)))

            result = [{"category_id": obj["product__category__id"],\
                       "count":       obj["count"]}\
                        for obj in orders]
        elif group_by_field=="product":
            orders = orders.annotate(product_id=F('product__id'))\
                           .values('product__id')\
                           .annotate(count=Count('product__id'))\
                           .exclude(count__lte=0)

            result = [{"product_id":  obj["product__id"],\
                       "count":       obj["count"]}\
                       for obj in orders]
        else:
            orders = None
            result = [{"count": 0}]
    else:
        # return length of the query set
        return JsonResponse({"data": {"count": len(orders)} })

    return JsonResponse({"data": result})


def product_summary_view(request):

    group_by = True if "group_by" in request.GET else False
    code = request.GET["code"] if "code" in request.GET else None
    category_name = request.GET["category_name"] if "category_name" in request.GET else None

    products = Product.objects.filter().exclude(deleted=1)
    products = products.filter(code=code) if code else products
    products = products.filter(category__name=category_name) if category_name else products
    products = products.annotate(category_id=F('category__id'))\
                       .values('category__id') \
                       .annotate(count=Count('category__id'))\
                       .exclude(count__lte=0)\
                       if group_by else products

    if group_by:
        result = [
            {
                "count": obj["count"],
                "category_id": obj["category__id"]
            } for obj in products
        ]
    else:
        result = {"count": len(products)}

    return JsonResponse({"data": result})

def health(request):
    return HttpResponse(status=200)
